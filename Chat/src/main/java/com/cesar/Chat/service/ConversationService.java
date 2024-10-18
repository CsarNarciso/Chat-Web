package com.cesar.Chat.service;

import com.cesar.Chat.dto.*;
import com.cesar.Chat.entity.Conversation;
import com.cesar.Chat.repository.ConversationRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@EnableCaching
@Service
public class ConversationService {

    public void create(ConversationDTO conversation, MessageForInitDTO message){

        List<Long> createFor = new ArrayList<>();
        Conversation savedEntity = new Conversation();

        //If request is not for recreation
        if(conversation==null){

            //Get message participants IDs
            List<Long> usersIds = Stream
                    .of(
                            message.getSenderId(),
                            message.getRecipientId())
                    .toList();

            //Look for an existent conversation between both users
            Conversation existentConversation = getByParticipantsIds(usersIds);

            //If don't exists
            if(existentConversation==null){

                //----CREATE----
                savedEntity = repo.save(
                        Conversation
                                .builder()
                                .createdAt(LocalDateTime.now())
                                .participantsIds(usersIds)
                                .build());
                conversation = mapper.map(savedEntity, ConversationDTO.class);
                createFor = conversation.getParticipantsIds();
            }
            else{

                //----RECREATE----
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

        //Store in Cache
        Conversation finalCreatedConversation = savedEntity;
        createFor
                .forEach(id -> {
                    redisTemplate.opsForList().rightPush(generateUserConversationsKey(id), finalCreatedConversation);
                });

        //----COMPOSE DATA----
        //Set participants' user/presence details, last message data and unread messages counts
        injectConversationsDetails(Stream.of(conversation).toList(), message.getSenderId());


        //----PUBLISH EVENT - ConversationCreated----
        kafkaTemplate.send("ConversationCreated", ConversationCreatedDTO
                .builder()
                .conversationId(conversation.getId())
                .createFor(createFor)
                .build());

        //----SEND CONVERSATION DATA----
        for (Long participantId : createFor) {
            messagingTemplate.convertAndSendToUser(participantId.toString(), "/user/reply", conversation);
        }
    }


    public List<ConversationDTO> load(Long userId){

        //Try to fetch from Cache
        String userConversationsKey = generateUserConversationsKey(userId);

        List<Conversation> conversations = Objects.requireNonNull(redisTemplate.opsForList()
                        .range(userConversationsKey, 0, -1))
                .stream()
                .map(c -> (Conversation) c)
                .toList();

        //Check for missing cache
        if(conversations.isEmpty()){

            //Then get from DB
            conversations = repo.findByParticipantId(userId);

            //And store in Cache
            redisTemplate.opsForList().rightPushAll(userConversationsKey, conversations);
        }

        //Set participants' user/presence details, last message data and unread messages counts
        List<ConversationDTO> conversationDTOS = mapToDTO(conversations);
        injectConversationsDetails(conversationDTOS, userId);

        return conversationDTOS;
    }




    public ConversationDTO delete(Long conversationId, Long participantId){

        //Look for conversation
        Conversation conversation = getById(conversationId);
        boolean permanently;

        //If exists...
        if(conversation!=null){

            //Get conversation participants Ids
            List<Long> participantsIds = conversation.getParticipantsIds();

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
                messageService.onConversationDeleted(conversationId, participantsIds);
            }
            //If not,
            else{

                //Deletion is local
                permanently = false;

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
                .conversationId(conversationId)
                .participantId(participantId)
                .build());

        return mapper.map(conversation, ConversationDTO.class);
    }




    private void injectConversationsDetails(List<ConversationDTO> conversations, Long participantId){

        List<Long> participantsIds = new ArrayList<>();
        List<Long> conversationsIds = mapToIds(conversations);

        for (ConversationDTO conversation : conversations) {

            //Get recipients participants (conversation face for sender)

            Long recipientId = conversation.getParticipantsIds()
                    .stream()
                    .takeWhile(id -> id != participantId)
                    .findFirst().orElse(null);
            participantsIds.add(recipientId);

            conversation.setRecipient(
                    ParticipantDTO
                            .builder()
                            .userId(recipientId)
                            .build());
        }

        //Set participants user details
        userService.injectConversationsParticipantsDetails(conversations, participantsIds);

        //Set participants presence statuses
        presenceService.injectConversationsParticipantsStatuses(conversations, participantsIds);

        //Set new unread message for each participant (less for sender)
        messageService.injectConversationsUnreadMessages(conversations, conversationsIds, participantId);
    }



    private Conversation getByParticipantsIds(List<Long> participantsIds){
        return repo.findByParticipantsIds(participantsIds);
    }



    public Conversation getById(Long id){
        return repo.getReferenceById(id);
    }



    private List<ConversationDTO> mapToDTO(List<Conversation> conversations){
        return conversations
                .stream()
                .map(c -> mapper.map(c, ConversationDTO.class))
                .toList();
    }

    private List<Long> mapToIds(List<ConversationDTO> conversations){
        return conversations
                .stream()
                .map(ConversationDTO::getId)
                .toList();
    }





    @KafkaListener(topics = "UserUpdated", groupId = "${spring.kafka.consumer.group-id}")
    public void onUserUpdate(Long userId){
        userService.invalidate(userId);
    }

    @KafkaListener(topics = "UserDeleted", groupId = "${spring.kafka.consumer.group-id}")
    public void onUserDelete(Long userId){
        userService.invalidate(userId);
    }



    private String generateUserConversationsKey(Long userId){
        return String.format("%s:conversations", userId);
    }

    @Autowired
    private ConversationRepository repo;
    @Autowired
    private PresenceService presenceService;
    @Autowired
    private MessageService messageService;
    @Autowired
    private UserService userService;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private ModelMapper mapper;
}