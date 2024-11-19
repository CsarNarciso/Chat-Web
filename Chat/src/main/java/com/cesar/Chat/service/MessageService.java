package com.cesar.Chat.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
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
                    .contains(senderId)){

            	//Save
            	Message entity = mapper.map(messageRequest, Message.class);
            	entity.setId(UUID.randomUUID());
                entity.setRead(false);
                entity.setSentAt(LocalDateTime.now());
            	repo.save(entity);
            	
            	MessageDTO message = mapToDTO(entity);

                //Send
                webSocketTemplate.convertAndSend(
                        String.format("/topic/conversation/%s",conversationId),
                        message);

                //Increment Unread Message in Cache

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
        List<MessageDTO> messages = new ArrayList<>();
        return messages;
    }



    public void cleanConversationUnreadMessages(UUID conversationId, Long participantId){
        repo.cleanConversationUnreadMessages(participantId, conversationId);
    }



    public void onUserDeleted(Long userId, List<UUID> conversationIds) {
    	repo.deleteByUserId(userId);
    }



    public void onConversationDeleted(UUID conversationId, Long participantId){
        //Mark unread messages in DB as read (for participant)
        repo.cleanConversationUnreadMessages(participantId, conversationId);
    }



    public void injectConversationsMessagesDetails(List<ConversationViewDTO> conversations,
                                                   List<UUID> conversationIds,
                                                   Long senderId){
        //Fetch last messages
        Map<UUID, LastMessageDTO> lastMessages = getLastMessages(senderId, conversationIds);

        //Fetch unreadMessages
        Map<UUID, Long> unreadMessages =
                getUnreadMessages(senderId, conversationIds);

        //If data exists
        if(!lastMessages.isEmpty() || !unreadMessages.isEmpty()) {
        	
        	//Match data with conversations
        	conversations
        	.forEach(conversation -> {
        		
        		UUID conversationId = conversation.getId();
        		
        		Long unreadMessagesCount = unreadMessages.get(conversationId);
        		
        		conversation.setUnreadMessagesCount( unreadMessagesCount != null ? unreadMessagesCount : 0 );
        		conversation.setLastMessage(lastMessages.get(conversationId));
        	});
        }
    }




    private Map<UUID, LastMessageDTO> getLastMessages(Long participantId, List<UUID> conversationIds){

        //Fetch last message details of each conversation
        Map<UUID, LastMessageDTO> lastMessages = new HashMap<>();
        List<MessageDTO> messages = repo.findAllByConversationIds(conversationIds);

        messages
        	.forEach(id -> {

	            //Filter its own messages
	            List<Message> missingConversationMessages = dbMessages
	                    .stream()
	                    .filter(m -> m.getConversation().getId().equals(id))
	                    .toList();
	
	            //And if there is something...
	            if(!missingConversationMessages.isEmpty()) {
	            	
	                //Get last message
	                lastMessages.put(id, 
						mapper.map(missingConversationMessages.getLast(), LastMessageDTO.class));
	            }
        	});
    	return lastMessages;
    }



    private Map<UUID, Long> getUnreadMessages(Long participantId, List<UUID> conversationIds){


        Map<UUID, Long> unreadMessages = new HashMap<>();

        List<UnreadMessagesDTO> counts = repo.getUnreadMessages(participantId, conversationIds);

        if(!counts.isEmpty()){

        	counts
        		.forEach(unreadMessage -> {
        		
	        		UUID conversationId = unreadMessage.getConversationId();
	        		Long count = unreadMessage.getCount();
	        		
	        		unreadMessages.put(conversationId, count);
        		});
        }
        return unreadMessages;
    }


    
    
    public Message createEntityOnSendRequest(MessageForSendDTO messageRequest) {
    	Message entity = mapper.map(messageRequest, Message.class);
    	entity.setId(UUID.randomUUID());
        entity.setRead(false);
        entity.setSentAt(LocalDateTime.now());
        return entity;
    }

    
    
    
    
    
    
    public MessageDTO mapToDTO(Message message){
        return mapper.map(message, MessageDTO.class);
    }
    
    private List<MessageDTO> mapToDTOs(List<Message> messages){
        return messages
                .stream()
                .filter(Objects::nonNull)
                .map(m -> mapper.map(m, MessageDTO.class))
                .toList();
    }
	
    


    
    

    public MessageService(@Lazy ConversationService conversationService, SimpMessagingTemplate webSocketTemplate, ModelMapper mapper) {
        this.conversationService = conversationService;
        this.webSocketTemplate = webSocketTemplate;
        this.mapper = mapper;
    }

    private final ConversationService conversationService;
    private final SimpMessagingTemplate webSocketTemplate;
    private final ModelMapper mapper;
}