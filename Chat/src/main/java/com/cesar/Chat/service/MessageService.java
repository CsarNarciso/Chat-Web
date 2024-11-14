package com.cesar.Chat.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import com.cesar.Chat.dto.ConversationDTO;
import com.cesar.Chat.dto.ConversationViewDTO;
import com.cesar.Chat.dto.LastMessageDTO;
import com.cesar.Chat.dto.MessageDTO;
import com.cesar.Chat.dto.MessageForInitDTO;
import com.cesar.Chat.dto.MessageForSendDTO;
import com.cesar.Chat.dto.UnreadMessagesDTO;
import com.cesar.Chat.entity.Conversation;
import com.cesar.Chat.entity.Message;
import com.cesar.Chat.entity.Participant;
import com.cesar.Chat.repository.MessageRepository;


@Service
public class MessageService {


    public void send(MessageForSendDTO messageRequest){

        Conversation conversation = conversationService.getById(messageRequest.getConversationId());

        //If conversation exists,
        if(conversation!=null){

            Long senderId = messageRequest.getSenderId();
            UUID conversationId = conversation.getId();

            //and user belongs to
            if(conversation.getParticipants()
                    .stream()
                    .map(Participant::getUserId)
                    .toList()
                    .contains(senderId)){

                Message entity = mapper.map(messageRequest, Message.class);
                entity.setRead(false);
                entity.setSentAt(LocalDateTime.now());

                //Save Message
                entity.setId(UUID.randomUUID());
                repo.save(entity);
				MessageDTO message = mapper.map(entity, MessageDTO.class);
				
                //In Cache
                String messagesKey = generateMessagesKey(conversationId);
                redisTemplate.opsForList().rightPush(messagesKey, message);

                //Send
                webSocketTemplate.convertAndSend(
                        String.format("/topic/conversation/%s",conversationId),
                        message);

                //Increment Unread Message in Cache
                String unreadKey = generateUnreadKey(senderId);
                globalRedisTemplate.opsForHash().increment(unreadKey, conversationId, 1);

                //If conversation needs to be recreated for someone
                if(!conversation.getRecreateFor().isEmpty()){
                    conversationService.create(
                            mapper.map(conversation, ConversationDTO.class),
                            mapper.map(messageRequest, MessageForInitDTO.class));
                }
            }
        }
    }



    public List<MessageDTO> loadConversationMessages(UUID conversationId) {

        String key = generateMessagesKey(conversationId);

        //Try to first fetch from Cache
        List<MessageDTO> cacheMessages = 
				redisTemplate.opsForList().range(key, 0, -1)
					.stream()
					.filter(Objects::nonNull)
					.toList();
					
        //If not in Cache
        if (cacheMessages.isEmpty()){
            //, then from DB
            cacheMessages = mapToDTOs(repo.findAllByConversationId(conversationId));
            //And store in Cache
			if(!cacheMessages.isEmpty()){
				redisTemplate.opsForList().rightPushAll(key, cacheMessages);
			}
        }
        return cacheMessages;
    }



    public UUID cleanConversationUnreadMessages(UUID conversationId, Long participantId){
        String key = generateUnreadKey(participantId);
        //In DB
        repo.cleanConversationUnreadMessages(participantId, conversationId);
        //In Cache
        globalRedisTemplate.opsForHash().put(key, conversationId, 0);
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
                    List<MessageDTO> cacheMessages = redisTemplate.opsForList().range(messageKey, 0, -1)
							.stream()
							.filter(Objects::nonNull)
                            .filter(m -> !m.getSenderId().equals(userId))
                            .toList();

                    //And update in Cache
                    redisTemplate.delete(messageKey);
                    redisTemplate.opsForList().rightPushAll(messageKey, cacheMessages);
                });

        //Unread Messages
        String unreadKey = generateUnreadKey(userId);
        globalRedisTemplate.delete(unreadKey);
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
        globalRedisTemplate.opsForHash().delete(unreadKey, conversationId.toString());
    }



    public void injectConversationsMessagesDetails(List<ConversationViewDTO> conversations,
                                                   List<UUID> conversationIds,
                                                   Long senderId){
        //Fetch last messages
        Map<UUID, LastMessageDTO> lastMessages = getLastMessages(senderId, conversationIds);

        //Fetch unreadMessages
        Map<UUID, Integer> unreadMessages =
                getUnreadMessages(senderId, conversationIds);

        //If data exists
        if(!lastMessages.isEmpty() || !unreadMessages.isEmpty()) {
        	
        	//Match data with conversations
        	conversations
        	.forEach(conversation -> {
        		
        		UUID conversationId = conversation.getId();
        		
        		Integer unreadMessagesCount = unreadMessages.get(conversationId);
        		
        		conversation.setUnreadMessagesCount( unreadMessagesCount != null ? unreadMessagesCount : 0 );
        		conversation.setLastMessage(lastMessages.get(conversationId));
        	});
        }
    }






    private Map<UUID, LastMessageDTO> getLastMessages(Long participantId, List<UUID> conversationIds){

        //Fetch last message details of each conversation
        Map<UUID, LastMessageDTO> lastMessages = new HashMap<>();
        List<UUID> missingCacheConversationMessagesIds = new ArrayList<>();

        conversationIds
                .forEach(id -> {

                    String conversationMessagesKey = generateMessagesKey(id);
                    MessageDTO cacheMessage = redisTemplate.opsForList().rightPop(conversationMessagesKey);
                    
                    LastMessageDTO lastMessage = cacheMessage!=null ? mapper.map(cacheMessage, LastMessageDTO.class) : null;

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

                        //And if there is something...
                        if(!missingConversationMessages.isEmpty()) {

                        	//Store in Cache
                        	String conversationMessagesKey = generateMessagesKey(id);
                        	
                            redisTemplate.opsForList().rightPushAll(
                                    conversationMessagesKey,
                                    mapToDTOs(missingConversationMessages));
                            
                            //Get last message
                            lastMessages.put(id, 
    							mapper.map(missingConversationMessages.getLast(), LastMessageDTO.class));
                        }
                    });
        }
        return lastMessages;
    }



    private Map<UUID, Integer> getUnreadMessages(Long participantId, List<UUID> conversationIds){

        String key = generateUnreadKey(participantId);
        Collection<Object> hashes = new HashSet<>(conversationIds.stream().map(UUID::toString).toList());

        Map<UUID, Integer> unreadMessages = new HashMap<>();
        List<Object> missingCacheCountsConversationIds = new ArrayList<>(conversationIds);

        //Fetch Conversation unread messages counts from Cache
        List<Object> counts = globalRedisTemplate.opsForHash().multiGet(key, hashes);

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
            globalRedisTemplate.opsForHash().putAll(key, cacheableCounts);
        }
        return unreadMessages;
    }



    private String generateMessagesKey(UUID conversationId){
        return String.format("conversation:%s:messages", conversationId);
    }
    private String generateUnreadKey(Long participantId){
        return String.format("user:%s:conversation:unreadMessages", participantId);
    }
	
	
	private List<MessageDTO> mapToDTOs(List<Message> messages){
        return messages
                .stream()
                .filter(Objects::nonNull)
                .map(m -> mapper.map(m, MessageDTO.class))
                .toList();
    }
	

    public MessageService(MessageRepository repo, @Lazy ConversationService conversationService, RedisTemplate<String, MessageDTO> redisTemplate, RedisTemplate<String, Object> globalRedisTemplate, SimpMessagingTemplate webSocketTemplate, ModelMapper mapper) {
        this.repo = repo;
        this.conversationService = conversationService;
        this.redisTemplate = redisTemplate;
        this.globalRedisTemplate = globalRedisTemplate;
        this.webSocketTemplate = webSocketTemplate;
        this.mapper = mapper;
    }

    private final MessageRepository repo;
    private final ConversationService conversationService;
    private final RedisTemplate<String, MessageDTO> redisTemplate;
    private final RedisTemplate<String, Object> globalRedisTemplate;
    private final SimpMessagingTemplate webSocketTemplate;
    private final ModelMapper mapper;
}