package com.cesar.Chat.service;

import com.cesar.Chat.dto.*;
import com.cesar.Chat.entity.Conversation;
import com.cesar.Chat.repository.ConversationRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

@EnableCaching
@Service
public class ConversationService {

    public void create(ConversationDTO conversation, MessageForInitDTO message){

        List<Long> createFor = new ArrayList<>();

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
                conversation = mapper.map(
                        repo.save(
                                Conversation
                                        .builder()
                                        .createdAt(LocalDateTime.now())
                                        .participantsIds(usersIds)
                                        .build()),
                        ConversationDTO.class);
                createFor = conversation.getParticipantsIds();
            }
            else{

                //----RECREATE----
                repo.save(
                        Conversation
                                .builder()
                                .recreateFor(null)
                                .build());
                createFor = existentConversation.getRecreateFor();
                conversation=mapper.map(existentConversation, ConversationDTO.class);
            }
        }

        //----COMPOSE DATA----
        //Set participants' user/presence details and unread messages counts
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

    public List<ConversationDTO> load(Long participantId){

        String userConversationsKey = generateUserConversationsKey(participantId);
        List<Conversation> conversations = new ArrayList<>();

        //Fetch user conversations Ids from Cache
        List<Long> conversationsIds = redisTemplate.opsForList().range(userConversationsKey, 0, -1)
                .stream()
                .map(id -> (Long) id)
                .toList();

        //If not in Cache
        if(conversationsIds.isEmpty()){

            //Then from DB
            conversations = getByParticipantId(participantId);

            conversationsIds = conversations
                    .stream()
                    .map(Conversation::getId)
                    .toList();

            //And store in Cache
        }
        else {

            Set<String> conversationKeys = new HashSet<>();

            conversationsIds
                    .forEach(conversationId -> {
                        conversationKeys.add(generateConversationKey(conversationId));
                    });

            conversations = redisTemplate.opsForValue().multiGet(conversationKeys)
                    .stream()
                    .map(c -> (Conversation) c)
                    .toList();

            List<Long> missingCacheConversationsIds = new ArrayList<>();

            if(!conversations.isEmpty()){
                conversations
                        .forEach(conversation -> {
                            if(conversation==null){
                                missingCacheConversationsIds.add(conversation.getId());
                            }
                        });
            }
            if(!missingCacheConversationsIds.isEmpty()){
                List<Conversation> missingConversations = repo.findAllById(missingCacheConversationsIds);
                conversations = conversations.stream().dropWhile(c -> c==null).toList();
                conversations.addAll(missingConversations);
                //Store them in Cache
                Map<String, Conversation> keysAndValues = new HashMap<>();
                for(int i = 0; i<missingCacheConversationsIds.size(); i++){
                    keysAndValues.put(generateConversationKey(
                            missingCacheConversationsIds.get(i)),
                            missingConversations.get(i));
                }
                redisTemplate.opsForValue().multiSet(keysAndValues);
            }
        }

        List<ConversationDTO> dtos = conversations
                .stream()
                .map(c -> mapper.map(c, ConversationDTO.class))
                .toList();

        //Set participants' user/presence details and unread messages counts
        injectConversationsDetails(dtos, participantId);

        return dtos;
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
                repo.deleteById(conversationId);
                redisTemplate.opsForHash().delete(REDIS_HASH_KEY, conversationId);
            }
            //If not,
            else{

                //Update recreateFor list
                permanently = false;
                repo.save(
                        Conversation
                                .builder()
                                .id(conversationId)
                                .recreateFor(recreateFor)
                                .build()
                );
            }
        }

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
        messageService.injectConversationsUnreadMessages(conversations, participantId);
    }

    @Cacheable
    private Conversation getByParticipantsIds(List<Long> participantsIds){
        return repo.findByParticipantsIds(participantsIds);
    }

    @Cacheable
    private List<Conversation> getByParticipantId(Long participantId){
        return repo.findByParticipantId(participantId);
    }

    @Cacheable
    public Conversation getById(Long id){
        return repo.getReferenceById(id);
    }



    @KafkaListener(topics = "UserUpdated", groupId = "${spring.kafka.consumer.group-id}")
    public void onUserUpdate(Long userId){
        userService.invalidate(userId);
    }

    @KafkaListener(topics = "UserDeleted", groupId = "${spring.kafka.consumer.group-id}")
    public void onUserDelete(Long userId){
        userService.invalidate(userId);
    }




    private String generateConversationKey(Long conversationId){
        return String.format("%s", conversationId);
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