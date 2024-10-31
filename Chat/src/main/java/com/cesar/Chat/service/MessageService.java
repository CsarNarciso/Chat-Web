package com.cesar.Chat.service;

import com.cesar.Chat.dto.*;
import com.cesar.Chat.entity.Conversation;
import com.cesar.Chat.entity.Message;
import com.cesar.Chat.entity.Participant;
import com.cesar.Chat.repository.MessageRepository;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;


@Service
@EnableCaching
public class MessageService {


    public void send(MessageForSendDTO message){

        Conversation conversation = conversationService.getById(message.getConversationId());

        //If conversation exists,
        if(conversation!=null){

            Long senderId = message.getSenderId();
            UUID conversationId = conversation.getId();

            //and user belongs to
            if(conversation.getParticipants()
                    .stream()
                    .map(Participant::getId)
                    .toList()
                    .contains(senderId)){

                Message entity = mapper.map(message, Message.class);
                entity.setRead(false);
                entity.setSentAt(LocalDateTime.now());

                //Save Message
                entity.setId(UUID.randomUUID());
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



    public List<MessageDTO> loadConversationMessages(UUID conversationId) {

        String key = generateMessagesKey(conversationId);

        //Fetch conversation messages from Cache
        List<Message> conversationMessages = redisTemplate.opsForList().range(key, 0, -1)
                .stream()
                .map(m -> (Message) m)
                .toList();

        //If not in Cache
        if (conversationMessages.isEmpty()){
            //, then from DB
            List<Message> dbMessages = repo.findAllByConversationId(conversationId);
            //And store in Cache
            redisTemplate.opsForList().rightPushAll(key, dbMessages);
            conversationMessages = dbMessages;
        }

        return  conversationMessages
                .stream()
                .map(m -> mapper.map(m, MessageDTO.class))
                .toList();
    }



    public UUID cleanConversationUnreadMessages(UUID conversationId, Long participantId){
        String key = generateUnreadKey(participantId);
        //In DB
        repo.cleanConversationUnreadMessages(participantId, conversationId);
        //In Cache
        redisTemplate.opsForHash().put(key, conversationId, 0);
        return conversationId;
    }



    public void onUserDeleted(Long userId, List<UUID> conversationIds) {

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



    public void onConversationDeleted(UUID conversationId, Long participantId, boolean permanently){

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
                                                   List<UUID> conversationIds,
                                                   Long senderId){
        //Fetch last messages
        Map<UUID, LastMessageDTO> lastMessages = getLastMessages(senderId, conversationIds);

        //Fetch unreadMessages
        Map<UUID, Integer> unreadMessages =
                getUnreadMessages(senderId, conversationIds);

        //Match data with conversations
        conversations
                .forEach(conversation -> {

                    UUID conversationId = conversation.getId();

                    conversation.setUnreadMessagesCount(unreadMessages.get(conversationId));
                    conversation.setLastMessage(lastMessages.get(conversationId));
                });
    }






    private Map<UUID, LastMessageDTO> getLastMessages(Long participantId, List<UUID> conversationIds){

        //Fetch last message details of each conversation
        Map<UUID, LastMessageDTO> lastMessages = new HashMap<>();
        List<UUID> missingCacheConversationMessagesIds = new ArrayList<>();

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
            List<Message> dbMessages = repo.findAllByConversationIds(missingCacheConversationMessagesIds);

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



    private Map<UUID, Integer> getUnreadMessages(Long participantId, List<UUID> conversationIds){

        String key = generateUnreadKey(participantId);
        Set<Object> hashes = new HashSet<>(conversationIds);

        Map<UUID, Integer> unreadMessages = new HashMap<>();
        List<UUID> missingCacheCountsConversationIds = new ArrayList<>();

        //Fetch Conversation unread messages counts from Cache
        List<Object> counts = redisTemplate.opsForHash().multiGet(key, hashes);
        missingCacheCountsConversationIds.addAll(conversationIds);

        if(!counts.isEmpty()){

            for(int i = 0; i < conversationIds.size(); i++){

                UUID conversationId = conversationIds.get(i);
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

                        UUID conversationId = unreadMessage.getConversationId();
                        Integer count = unreadMessage.getCount();

                        unreadMessages.put(conversationId, count);
                        cacheableCounts.put(conversationId.toString(), count);
                    });

            //And store in Cache
            redisTemplate.opsForHash().putAll(key, cacheableCounts);
        }
        return unreadMessages;
    }



    private String generateMessagesKey(UUID conversationId){
        return String.format("%s:messages", conversationId);
    }
    private String generateUnreadKey(Long participantId){
        return String.format("%s:unread", participantId);
    }


    public MessageService(MessageRepository repo, @Lazy ConversationService conversationService, RedisTemplate<String, Object> redisTemplate, SimpMessagingTemplate webSocketTemplate, ModelMapper mapper) {
        this.repo = repo;
        this.conversationService = conversationService;
        this.redisTemplate = redisTemplate;
        this.webSocketTemplate = webSocketTemplate;
        this.mapper = mapper;
    }

    private final MessageRepository repo;
    private final ConversationService conversationService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final SimpMessagingTemplate webSocketTemplate;
    private final ModelMapper mapper;
}