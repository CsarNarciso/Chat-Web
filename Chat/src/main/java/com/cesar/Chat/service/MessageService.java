package com.cesar.Chat.service;

import com.cesar.Chat.dto.*;
import com.cesar.Chat.entity.Conversation;
import com.cesar.Chat.entity.Message;
import com.cesar.Chat.repository.MessageRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;

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
                //In Cache
                String messagesKey = generateMessagesKey(conversationId);
                redisTemplate.opsForList().rightPush(messagesKey, entity);

                //Send
                webSocketTemplate.convertAndSend(
                        String.format("/topic/conversation/%s",conversationId),
                        message);

                //Increment Unread Message in Cache
                String unreadKey = generateUnreadKey(senderId);
                redisTemplate.opsForHash().increment(unreadKey, conversationId, 1);

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

        String key = generateMessagesKey(conversationId);

        //Fetch conversation messages from Cache
        List<Message> conversationMessages = redisTemplate.opsForList().range(key, 0, -1)
                .stream()
                .map(m -> (Message) m)
                .toList();

        //If not in Cache
        if (conversationMessages==null || conversationMessages.isEmpty()){
            //, then from DB
            List<Message> dbMessages = repo.findByConversationId(conversationId);
            //And store in Cache
            redisTemplate.opsForList().rightPushAll(key, dbMessages);
            conversationMessages = dbMessages;
        }

        return  conversationMessages
                .stream()
                .map(m -> mapper.map(m, MessageDTO.class))
                .toList();
    }



    public void cleanConversationUnreadMessages(Long conversationId, Long participantId){
        String key = generateUnreadKey(participantId);
        //In DB
        repo.cleanConversationUnreadMessages(participantId, conversationId);
        //In Cache
        redisTemplate.opsForHash().put(key, conversationId, 0);
    }



    public void onUserDeleted(Long userId, List<Long> conversationIds) {

        //Delete user messages in DB
        repo.deleteBySenderId(userId);

        //In Cache
        conversationIds
                .forEach(conversationId -> {

                    //For each user's conversation...
                    String messageKey = generateMessagesKey(conversationId);

                    //Get actual conversation messages list from Cache to filter out user messages
                    List<Message> messages = redisTemplate.opsForList().range(messageKey, 0, -1)
                            .stream()
                            .map(m -> (Message) m)
                            .dropWhile(m -> m.getSenderId().equals(userId))
                            .toList();

                    //And update in Cache
                    redisTemplate.delete(messageKey);
                    redisTemplate.opsForList().rightPushAll(messageKey, messages);
                });

        //Unread Messages
        String unreadKey = generateUnreadKey(userId);
        redisTemplate.delete(unreadKey);
    }



    public void onConversationDeleted(Long conversationId, Long participantId, boolean permanently){

        //If deletion is permanently (for all participants)...
        if(permanently){

            //Delete conversation messages in DB
            repo.deleteByConversationId(conversationId);

            //In Cache
            String conversationMessagesKey = generateMessagesKey(conversationId);
            redisTemplate.delete(conversationMessagesKey);
        }

        //Mark unread messages in DB as read (for participant)
        repo.cleanConversationUnreadMessages(participantId, conversationId);

        //And delete in Cache
        String unreadKey = generateUnreadKey(participantId);
        redisTemplate.opsForHash().delete(unreadKey, conversationId);
    }



    public void injectConversationsMessagesDetails(List<ConversationDTO> conversations,
                                                   List<Long> conversationIds,
                                                   Long senderId){
        //Fetch last messages
        Map<Long, LastMessageDTO> lastMessages = getLastMessages(senderId, conversationIds);

        //Fetch unreadMessages
        Map<Long, Integer> unreadMessages =
                getUnreadMessages(senderId, conversationIds);

        //Match data with conversations
        conversations
                .forEach(conversation -> {

                    Long conversationId = conversation.getId();

                    conversation.setUnreadMessagesCount(unreadMessages.get(conversationId));
                    conversation.setLastMessage(lastMessages.get(conversationId));
                });
    }






    private Map<Long, LastMessageDTO> getLastMessages(Long participantId, List<Long> conversationIds){

        //Fetch last message details of each conversation
        Map<Long, LastMessageDTO> lastMessages = new HashMap<>();
        List<Long> missingCacheConversationMessagesIds = new ArrayList<>();

        conversationIds
                .forEach(id -> {

                    String conversationMessagesKey = generateMessagesKey(id);

                    LastMessageDTO lastMessage = (LastMessageDTO) redisTemplate.opsForList().rightPop(
                            conversationMessagesKey);

                    if(lastMessage!=null){
                        lastMessages.put(id, lastMessage);
                    }
                    else {
                        missingCacheConversationMessagesIds.add(id);
                    }
                });

        //If missing cache
        if(!missingCacheConversationMessagesIds.isEmpty()){

            //Then, get all messages from DB
            List<Message> dbMessages = repo.findByConversationIds(missingCacheConversationMessagesIds);

            //And for each missing conversation
            missingCacheConversationMessagesIds
                    .forEach(id -> {

                        //Filter its own messages
                        List<Message> missingConversationMessages = dbMessages
                                .stream()
                                .filter(m -> m.getConversationId().equals(id))
                                .toList();

                        //And store in Cache
                        String conversationMessagesKey = generateMessagesKey(id);
                        redisTemplate.opsForList().rightPushAll(
                                conversationMessagesKey,
                                missingConversationMessages);

                        //Get last message
                        lastMessages.put(
                                id,
                                mapper.map(missingConversationMessages.getLast(), LastMessageDTO.class));
                    });
        }
        return lastMessages;
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



    private String generateMessagesKey(Long conversationId){
        return String.format("%s:messages", conversationId);
    }
    private String generateUnreadKey(Long participantId){
        return String.format("%s:unread", participantId);
    }



    @Autowired
    private MessageRepository repo;
    @Autowired
    private ConversationService conversationService;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private SimpMessagingTemplate webSocketTemplate;
    @Autowired
    private ModelMapper mapper;
}