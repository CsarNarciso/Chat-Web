package com.cesar.Chat.service;

import com.cesar.Chat.dto.*;
import com.cesar.Chat.entity.Conversation;
import com.cesar.Chat.entity.Message;
import com.cesar.Chat.repository.MessageRepository;
import io.lettuce.core.api.sync.RedisListCommands;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.serializer.Serializer;

import java.time.LocalDateTime;
import java.util.*;
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

                //Save Message
                repo.save(entity);
                redisTemplate.opsForHash().put(
                        REDIS_KEY,
                        generateMessageRedisKey(senderId, conversationId),
                        entity);

                //Send
                messagingTemplate.convertAndSend(
                        "/topic/conversation/"+conversation.getId(),
                        message);

                //Increment Unread Message
                String key = generateUnreadKey(senderId);
                redisTemplate.opsForHash().increment(key, conversationId, 1);

                //If conversation needs to be recreated for someone
                if(!conversation.getRecreateFor().isEmpty()){
                    conversationService.create(
                            mapper.map(conversation, ConversationDTO.class),
                            mapper.map(message, MessageForInitDTO.class));
                }
            }
        }
    }

    public List<MessageDTO> loadConversationMessages(Long conversationId) {

        String key = String.format("conversation:%s:messages", conversationId);

        //Fetch participant messages from Cache
        List<Message> conversationMessages = redisTemplate.opsForList().range(key, 0, -1);

        //If not in Cache
        if (conversationMessages==null || conversationMessages.isEmpty()){
            //, then from DB
            List<Message> messagesFromDB = repo.findByConversationId(conversationId);
            //And store in Cache
            redisTemplate.opsForList().rightPushAll(key, messagesFromDB);
            conversationMessages = messagesFromDB;
        }

        return  conversationMessages
                .stream()
                .map(m -> mapper.map(m, MessageDTO.class))
                .toList();
    }


    private Map<Long, Integer> getUnreadMessages(Long participantId, List<Long> conversationIds){

        String key = generateUnreadKey(participantId);
        Set<Object> hashes = new HashSet<>(conversationIds);

        Map<Long, Integer> unreadMessages = new HashMap<>();
        List<Long> missingCacheCountsConversationIds = new ArrayList<>();

        //Fetch Conversation unread messages counts from Cache
        List<Object> counts = redisTemplate.opsForHash().multiGet(key, hashes);
        missingCacheCountsConversationIds.addAll(conversationIds);

        if(!counts.isEmpty()){

            for(int i = 0; i < conversationIds.size(); i++){

                Long conversationId = conversationIds.get(i);
                Integer count = (Integer) counts.get(i);

                if(count!=null){
                    unreadMessages.put(conversationId, count);
                    missingCacheCountsConversationIds.removeIf(id->id.equals(conversationId));
                }
            }
        }

        //If there is missing cache...
        if(!missingCacheCountsConversationIds.isEmpty()){

            //Get from DB
            List<UnreadMessagesDTO> dbCounts = repo.getUnreadMessages(participantId, conversationIds);
            Map<String, Integer> cacheableCounts = new HashMap<>();

            dbCounts
                    .forEach(unreadMessage -> {

                        Long conversationId = unreadMessage.getConversationId();
                        Integer count = unreadMessage.getCount();

                        unreadMessages.put(conversationId, count);
                        cacheableCounts.put(conversationId.toString(), count);
                    });

            //And store in Cache
            redisTemplate.opsForHash().putAll(key, cacheableCounts);
        }
        return unreadMessages;
    }

    public void cleanConversationUnreadMessages(Long conversationId, Long participantId){
        String key = generateUnreadKey(participantId);
        //In DB
        repo.cleanConversationUnreadMessages(participantId, conversationId);
        //In Cache
        redisTemplate.opsForHash().put(key, conversationId, 0);
    }

    public void onUserDeleted(Long userId) {

        //Delete user messages
        repo.deleteBySenderId(userId);
        redisTemplate.opsForHash().delete(REDIS_KEY, generateMessageRedisKey(userId,null));

        //Unread Messages
        String key = generateUnreadKey(userId);
        redisTemplate.opsForHash().delete(key, "*");
    }

    public void onConversationDeleted(Long conversationId, List<Long> participantsIds){

        //Delete conversation messages
        repo.deleteByConversationId(conversationId);
        redisTemplate.opsForHash().delete(REDIS_KEY, generateMessageRedisKey(null, conversationId));

        //Unread Messages
        participantsIds
                .forEach(participantId -> {
                    String key = generateUnreadKey(participantId);
                    redisTemplate.opsForHash().delete(key, conversationId);
                });
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




    private String generateUnreadKey(Long participantId){
        return String.format("unread:%s", participantId);
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
    private RedisListCommands<String, Object> redisListCommands;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private ModelMapper mapper;
}