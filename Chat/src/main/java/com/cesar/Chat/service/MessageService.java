package com.cesar.Chat.service;

import com.cesar.Chat.dto.*;
import com.cesar.Chat.entity.Conversation;
import com.cesar.Chat.entity.Message;
import com.cesar.Chat.repository.MessageRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@EnableCaching
@Service
public class MessageService {

    public void send(MessageForSendDTO message){

        Conversation conversation = conversationService.getById(message.getConversationId());

        //If conversation exists,
        if(conversation!=null){

            Long senderId = message.getSenderId();
            Long conversationId = conversation.getId();

            //and user belongs to
            if(conversation.getParticipantsIds().contains(senderId)){

                Message entity = mapper.map(message, Message.class);
                entity.setRead(false);
                entity.setSentAt(LocalDateTime.now());

                //Add new unread message to Conversation Participant UnreadMessagesDTO
                redisTemplate.opsForHash().increment(
                        REDIS_HASH_KEY,
                        generateUnreadMessagesRedisKey(senderId, conversationId),
                        1);

                //Save Message
                repo.save(entity);
                redisTemplate.opsForHash().put(
                        REDIS_HASH_KEY,
                        generateMessageRedisKey(senderId, conversationId),
                        entity);

                //Send
                messagingTemplate.convertAndSend(
                        "/topic/conversation/"+conversation.getId(),
                        message);

                //If conversation needs to be recreated for someone
                if(!conversation.getRecreateFor().isEmpty()){
                    conversationService.create(
                            mapper.map(conversation, ConversationDTO.class),
                            mapper.map(message, MessageForInitDTO.class));
                }
            }
        }
    }

    public List<MessageDTO> loadConversationMessages(Long conversationId){

        //Fetch Conversation messages from Cache
        List<Message> messages = (List<Message>) redisTemplate.opsForHash().get(REDIS_HASH_KEY,
                generateMessageRedisKey(null, conversationId));

        //If not in Cache, then from DB
        messages = (messages==null || messages.isEmpty()) ? repo.findByConversationId(conversationId) : messages;

        //And store in Cache
        Map<String, Message> cacheableValues = new HashMap<>();
        messages
                .forEach(message -> {
                    cacheableValues.put(generateMessageRedisKey(message.getSenderId(), conversationId), message);
                });
        redisTemplate.opsForHash().putAll(REDIS_HASH_KEY, cacheableValues);

        return  messages
                .stream()
                .map(m -> mapper.map(m, MessageDTO.class))
                .toList();
    }

    private Map<Long, Integer> getUnreadMessages(Long participantId){

        //Fetch Conversation messages from Cache
        Map<Long, Integer> unreadMessages = (Map<Long, Integer>) redisTemplate.opsForHash().get(
                REDIS_HASH_KEY,
                generateUnreadMessagesRedisKey(participantId, null));

        //If not in Cache
        if(unreadMessages==null || unreadMessages.isEmpty()){

            //, then from DB
            repo.getUnreadMessages(participantId)
                    .forEach(unreadMessage -> {
                        unreadMessages.put(unreadMessage.getConversationId(), unreadMessage.getCount());
                    });
        }

        //And store in Cache
        Map<String, Integer> cacheableValues = new HashMap<>();
        unreadMessages
                .forEach((conversationId, count) -> {
                    cacheableValues.put(generateUnreadMessagesRedisKey(participantId, conversationId), count);
                });
        redisTemplate.opsForHash().putAll(REDIS_HASH_KEY, cacheableValues);

        return unreadMessages;
    }

    public void cleanConversationUnreadMessages(Long conversationId, Long participantId){
        //In DB and Cache
        repo.cleanConversationUnreadMessages(conversationId, participantId);
        redisTemplate.opsForHash().put(REDIS_HASH_KEY,
                generateUnreadMessagesRedisKey(participantId, conversationId), 0);
    }

    public void onUserDeleted(Long userId) {

        //Delete user messages

        //On DB
        repo.deleteBySenderId(userId);

        //On Cache
        redisTemplate.opsForHash().delete(REDIS_HASH_KEY, generateMessageRedisKey(userId,null));

        //Unread Messages
        redisTemplate.opsForHash().delete(REDIS_HASH_KEY, generateUnreadMessagesRedisKey(userId, null));
    }

    public void onConversationDeleted(Long conversationId){

        //Delete conversation messages

        //On DB
        repo.deleteByConversationId(conversationId);

        //On Cache
        redisTemplate.opsForHash().delete(REDIS_HASH_KEY, generateMessageRedisKey(null, conversationId));

        //Unread Messages
        redisTemplate.opsForHash().delete(REDIS_HASH_KEY,
                generateUnreadMessagesRedisKey(null, conversationId));
    }

    public void injectConversationsUnreadMessages(List<ConversationDTO> conversations, Long senderId){

        //Fetch unreadMessages
        Map<Long, Integer> unreadMessages =
                getUnreadMessages(senderId);

        //Match unreadMessages with conversations
        conversations
                .forEach(conversation -> {
                    conversation.setUnreadMessages(unreadMessages.get(conversation.getId()));
                });
    }

    private String generateUnreadMessagesRedisKey(Long senderId, Long conversationId){
        return String.format("unread:%s:%s",
                senderId==null ? "*" : senderId,
                conversationId==null ? "*" : conversationId);
    }
    private String generateMessageRedisKey(Long senderId, Long conversationId){
        return String.format("%s:%s",
                senderId==null ? "*" : senderId,
                conversationId==null ? "*" : conversationId);
    }

    @Autowired
    private MessageRepository repo;
    @Autowired
    private ConversationService conversationService;
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private ModelMapper mapper;

    private static final String REDIS_HASH_KEY = "Message";
}