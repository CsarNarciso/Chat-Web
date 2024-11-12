package com.cesar.Chat.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.modelmapper.ModelMapper;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.cesar.Chat.dto.ConversationCreatedDTO;
import com.cesar.Chat.dto.ConversationDTO;
import com.cesar.Chat.dto.ConversationDeletedDTO;
import com.cesar.Chat.dto.MessageForInitDTO;
import com.cesar.Chat.entity.Conversation;
import com.cesar.Chat.entity.Participant;
import com.cesar.Chat.repository.ConversationRepository;

@Service
public class ConversationService {


    public void create(ConversationDTO conversation, MessageForInitDTO message){

        List<Long> createFor = new ArrayList<>();
        Conversation savedEntity = Conversation.builder().build();

        //If request is not for recreation
        if(conversation==null){

            //Get message participant IDs
            List<Long> userIds = Stream
                    .of(
                            message.getSenderId(),
                            message.getRecipientId())
                    .toList();

            //Look for an existent conversation between both users
            Conversation existentConversation = getByUserIds(userIds, userIds.size());

            //If don't exists
            if(existentConversation==null){

                //Create

            	//Conversation (no participants yet)
                savedEntity = repo.save(
                        Conversation
                                .builder()
                                .id(UUID.randomUUID())
                                .createdAt(LocalDateTime.now())
                                .participants(new ArrayList<>())
                                .build());
                
                //Then, participants
                savedEntity.setParticipants(participantService.createAll(userIds, savedEntity));
                
                conversation = mapper.map(savedEntity, ConversationDTO.class);
                createFor = userIds;
            }
            else{

                //Recreate
                createFor = existentConversation.getRecreateFor();
                existentConversation = repo.save(
                        Conversation
                                .builder()
                                .id(existentConversation.getId())
                                .recreateFor(null)
                                .build());
                savedEntity = existentConversation;
                conversation = mapper.map(existentConversation, ConversationDTO.class);
            }
        }

        //Store conversations and participants in Cache (separately)
        createFor
                .forEach(id -> {
                    redisTemplate.opsForList().rightPush(generateUserConversationsKey(id), conversation);
                    redisTemplate.opsForList().rightPush(generateConversationParticipantsKey(), savedEntity.getParticipants());
                });

        //Compose conversation participants' user/presence details, last message data and unread message counts
        injectConversationsDetails(Stream.of(savedEntity).toList(), message.getSenderId());


        //Event publisher - ConversationCreated
        kafkaTemplate.send("ConversationCreated",
                ConversationCreatedDTO.builder()
                .id(conversation.getId())
                .createFor(createFor)
                .build());

        //Send participants' custom conversation data
        createFor.forEach(participantId -> {
            webSocketTemplate.convertAndSendToUser(
                    participantId.toString(),
                    "/user/reply/createConversation",
                    conversation);
        });
    }


    public List<ConversationFaceDTO> load(Long userId){
        //Set participants' user/presence details, last message data and unread messages counts
        return injectConversationsDetails(getByUserId(userId), userId);
    }



    public ConversationFaceDTO delete(UUID conversationId, Long participantId){

        //Look for conversation
        Conversation conversation = getById(conversationId);
        boolean permanently = false;

        //If exists...
        if(conversation!=null){

            //Get conversation participant IDs
            List<Long> participantsIds = conversation.getParticipants()
                    .stream()
                    .map(Participant::getUserId)
                    .toList();

            //Add userId to conversation recreateFor list
            List<Long> recreateFor = conversation.getRecreateFor();
            recreateFor.add(participantId);

            //If recreateFor matches participantsIds...
            if(participantsIds.equals(recreateFor)){

                //Deletion is permanently
                permanently = true;

                //Delete in DB
                repo.deleteById(conversationId);

                //In Cache
                Set<String> userConversationsKeys = participantsIds
                        .stream()
                        .map(this::generateUserConversationsKey)
                        .collect(Collectors.toSet());
                redisTemplate.delete(userConversationsKeys);

                //Ask Message service to delete deleted conversation messages/unread counts
                messageService.onConversationDeleted(conversationId, participantId, permanently);
            }
            //If not,
            else{

                //Deletion is local

                //Update recreateFor list
                repo.save(
                        Conversation
                                .builder()
                                .id(conversationId)
                                .recreateFor(recreateFor)
                                .build()
                );
                //Delete in Cache
                String userConversationsKey = generateUserConversationsKey(participantId);
                redisTemplate.delete(userConversationsKey);
            }
        }
        //Ask Message service to delete deleted conversation messages/unread counts
        messageService.onConversationDeleted(conversationId, participantId, permanently);

        //----PUBLISH EVENT - ConversationDeleted----
        kafkaTemplate.send("ConversationDeleted", ConversationDeletedDTO
                .builder()
                .id(conversationId)
                .participantId(participantId)
                .permanently(permanently)
                .build());

        return mapper.map(conversation, ConversationFaceDTO.class);
    }


    private List<ConversationFaceDTO> injectConversationsDetails(List<Conversation> conversations,
                                                             Long participantId){
        List<Long> recipientIds = new ArrayList<>();
        List<UUID> conversationIds = mapToIds(conversations);
        List<ConversationFaceDTO> conversationDTOS = mapToDTO(conversations);

        //For each conversation
        for (int i = 0; i < conversations.size(); i++){

            //Get and set recipient reference (conversation face for sender)
            Long recipientId = conversations.get(i).getParticipants()
                    .stream()
                    .map(Participant::getUserId)
                    .filter(id -> !id.equals(participantId))
                    .findFirst().orElse(null);
            
            recipientIds.add(recipientId);

            conversationDTOS.get(i).setRecipient(
                    ParticipantFaceDTO
                            .builder()
                            .userId(recipientId)
                            .build());
        }
        //Set participants user details
        userService.injectConversationsParticipantsDetails(conversationDTOS, recipientIds);

        //Set participants presence statuses
        presenceService.injectConversationsParticipantsStatuses(conversationDTOS, recipientIds);

        //Set last messages data and unread messages counts
        messageService.injectConversationsMessagesDetails(
                conversationDTOS, conversationIds, participantId);

        return conversationDTOS;
    }



    private List<Conversation> getByUserId(Long userId){

        //Try to fetch from Cache
        String userConversationsKey = generateUserConversationsKey(userId);

        List<Conversation> conversations = redisListTemplate.range(userConversationsKey, 0, -1)
								.stream()
								.filter(Objects::nonNull)
								.toList();

        //Check for missing cache
        if(conversations.isEmpty()){

            //Then get from DB
            conversations = repo.findByUserId(userId);

            //And store in Cache
            if(!conversations.isEmpty()){
                redisListTemplate.rightPushAll(userConversationsKey, conversations);
            }
        }
        return conversations;
    }

    public Conversation getById(UUID id){
        return repo.getReferenceById(id);
    }

    private Conversation getByUserIds(List<Long> userIds, int userCount){
        return repo.findByUserIds(userIds, userCount);
    }


    @KafkaListener(topics = "UserDeleted", groupId = "${spring.kafka.consumer.group-id}")
    public void onUserDeleted(Long id){

        //Invalidate user conversations in cache
        String userConversationsKey = generateUserConversationsKey(id);
        List<Conversation> conversations = getByUserId(id);
        List<UUID> conversationsIds = mapToIds(conversations);
        redisTemplate.delete(userConversationsKey);

        //Invalidate user messages and unread counts
        messageService.onUserDeleted(id, conversationsIds);
    }





    private String generateUserConversationsKey(Long userId){
        return String.format("%s:conversations", userId);
    }

    private List<ConversationFaceDTO> mapToDTO(List<Conversation> conversations){
        return conversations
                .stream()
                .map(c -> mapper.map(c, ConversationFaceDTO.class))
                .toList();
    }

    private List<UUID> mapToIds(List<Conversation> conversations){
        return conversations
                .stream()
                .map(Conversation::getId)
                .toList();
    }




    public ConversationService(ConversationRepository repo, ParticipantService participantService, MessageService messageService, PresenceService presenceService, UserService userService, RedisTemplate<String, ConversationDTO> redisTemplate, KafkaTemplate<String, Object> kafkaTemplate, SimpMessagingTemplate webSocketTemplate, ModelMapper mapper) {
        this.repo = repo;
        this.messageService = messageService;
        this.participantService = participantService;
        this.presenceService = presenceService;
        this.userService = userService;
        this.redisTemplate = redisTemplate;
        this.redisListTemplate = redisTemplate.opsForList();
        this.kafkaTemplate = kafkaTemplate;
        this.webSocketTemplate = webSocketTemplate;
        this.mapper = mapper;
    }

    private final ConversationRepository repo;
    private final ParticipantService participantService;
    private final MessageService messageService;
    private final PresenceService presenceService;
    private final UserService userService;
    private final RedisTemplate<String, ConversationDTO> redisTemplate;
    private final ListOperations<String, ConversationDTO> redisListTemplate;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final SimpMessagingTemplate webSocketTemplate;
    private final ModelMapper mapper;
}