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
import com.cesar.Chat.dto.ConversationRecipientDTO;
import com.cesar.Chat.dto.ConversationViewDTO;
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
                createFor = userIds;

            	//Conversation (no participants yet)
                savedEntity = repo.save(
                        Conversation
                                .builder()
                                .id(UUID.randomUUID())
                                .createdAt(LocalDateTime.now())
                                .participants(new ArrayList<>())
                                .build());
                //Store in Cache
                for(Long id : createFor) {
          				redisListTemplate.rightPush(generateUserConversationsKey(id), conversation);
              		};
                
                //And then participants
                savedEntity.setParticipants(participantService.createAll(userIds, savedEntity));
                
                conversation = mapper.map(savedEntity, ConversationDTO.class);
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

        //For everyone involved in the creation/recreation
        for(Long participantId : createFor){
                	
                	//Compose custom conversation view: recipient presence/details, last message data and unread message count
                    ConversationViewDTO conversationView = 
                    		composeConversationsData(Stream.of(savedEntity).toList(), message.getSenderId()).getFirst();
                	
                    //Send
                    webSocketTemplate.convertAndSendToUser(
                            participantId.toString(),
                            "/user/reply/createConversation",
                            conversationView);
                };

        //Event publisher - ConversationCreated
        kafkaTemplate.send("ConversationCreated", ConversationCreatedDTO
	        		.builder()
	                .id(conversation.getId())
	                .createFor(createFor)
	                .build());
    }


    public List<ConversationViewDTO> load(Long userId){
        //Set participants' user/presence details, last message data and unread messages counts
        return composeConversationsData(getAllByUserId(userId), userId);
    }



    public ConversationViewDTO delete(UUID conversationId, Long participantId){

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

        return mapper.map(conversation, ConversationViewDTO.class);
    }


    private List<ConversationViewDTO> composeConversationsData(List<Conversation> conversations,
                                                             Long conversationViewOwnerId){
        List<Long> recipientIds = new ArrayList<>();
        List<UUID> conversationIds = mapToIds(conversations);
        List<ConversationViewDTO> conversationViews = mapToViewDTOs(conversations);

        //For each conversation
        for (int i = 0; i < conversations.size(); i++){

            //Get recipient reference (conversation face for sender)
            Long recipientId = conversations.get(i).getParticipants()
                    .stream()
                    .map(Participant::getUserId)
                    .filter(id -> !id.equals(conversationViewOwnerId))
                    .findFirst().orElse(null);
            recipientIds.add(recipientId);

            //And compose as part of conversation view
            conversationViews.get(i).setRecipient(
                    ConversationRecipientDTO
                            .builder()
                            .userId(recipientId)
                            .build());
        }
        //Inject recipients' user details
        userService.injectConversationsParticipantsDetails(conversationViews, recipientIds);

        //, presence statuses
        presenceService.injectConversationsParticipantsStatuses(conversationViews, recipientIds);

        //And view owner last messages and unread message counts
        messageService.injectConversationsMessagesDetails(conversationViews, conversationIds, conversationViewOwnerId);

        return conversationViews;
    }



    private List<Conversation> getAllByUserId(Long userId){

        //Try to fetch from Cache
        String userConversationsKey = generateUserConversationsKey(userId);

        List<ConversationDTO> conversations = redisListTemplate.range(userConversationsKey, 0, -1)
								.stream()
								.filter(Objects::nonNull)
								.toList();
								
        //Check for missing cache
        if(conversations.isEmpty()){

            //Then get from DB
            conversations = mapToDTOs(repo.findByUserId(userId));

            //And store in Cache
            if(!conversations.isEmpty()){
                redisListTemplate.rightPushAll(userConversationsKey, conversations);
            }
        }
        return conversations
				.stream()
				.map(c -> mapper.map(c, Conversation.class))
				.toList();
    }

    public Conversation getById(UUID id){
        return repo.findById(id).orElse(null);
    }

    private Conversation getByUserIds(List<Long> userIds, int userCount){
        return repo.findByUserIds(userIds, userCount);
    }


    @KafkaListener(topics = "UserDeleted", groupId = "${spring.kafka.consumer.group-id}")
    public void onUserDeleted(Long id){

        //Invalidate user conversations in cache
        String userConversationsKey = generateUserConversationsKey(id);
        List<Conversation> conversations = getAllByUserId(id);
        List<UUID> conversationsIds = mapToIds(conversations);
        redisTemplate.delete(userConversationsKey);

        //Invalidate user messages and unread counts
        messageService.onUserDeleted(id, conversationsIds);
    }





    private String generateUserConversationsKey(Long userId){
        return String.format("user:%s:conversations", userId);
    }

    private List<ConversationDTO> mapToDTOs(List<Conversation> conversations){
        return conversations
                .stream()
                .map(c -> mapper.map(c, ConversationDTO.class))
                .toList();
    }
    
    private List<ConversationViewDTO> mapToViewDTOs(List<Conversation> conversations){
        return conversations
                .stream()
                .map(c -> mapper.map(c, ConversationViewDTO.class))
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