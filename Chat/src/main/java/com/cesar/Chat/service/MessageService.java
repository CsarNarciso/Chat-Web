package com.cesar.Chat.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.cesar.Chat.dto.ConversationViewDTO;
import com.cesar.Chat.dto.LastMessageDTO;
import com.cesar.Chat.dto.MessageDTO;
import com.cesar.Chat.dto.MessageForInitDTO;
import com.cesar.Chat.dto.MessageForSendDTO;
import com.cesar.Chat.dto.UnreadMessagesDTO;
import com.cesar.Chat.entity.Message;


@Service
public class MessageService {


    public void send(MessageForSendDTO messageRequest) {

        UUID conversationId = messageRequest.getConversationId();

    	//Save
    	Message entity = mapper.map(messageRequest, Message.class);
    	entity.setId(UUID.randomUUID());
        entity.setRead(false);
        entity.setSentAt(LocalDateTime.now());
    	MessageDTO message = dataService.save(entity, conversationId);
    	
        //Send
        webSocketTemplate.convertAndSend(
                String.format("/topic/conversation/%s", conversationId),
                message);

        //Check if conversation needs to be recreated for someone
        if(messageRequest.isRecreateForSomeone()) {
            conversationService.create(conversationId, mapper.map(messageRequest, MessageForInitDTO.class));
        }
    }

    public Message createEntityOnSendRequest(MessageForSendDTO messageRequest) {
    	Message entity = mapper.map(messageRequest, Message.class);
    	entity.setId(UUID.randomUUID());
        entity.setRead(false);
        entity.setSentAt(LocalDateTime.now());
        return entity;
    }
    
    public List<MessageDTO> loadConversationMessages(UUID conversationId) {
        return dataService.getAllByConversationId(conversationId);
    }

    public void markMessagesAsRead(UUID conversationId, Long participantId) {
        dataService.markMessagesAsRead(conversationId, participantId);
    }

    public void onConversationDeleted(UUID conversationId, Long participantId) {
    	dataService.markMessagesAsRead(conversationId, participantId);
    }

    public void injectConversationsMessagesDetails(List<ConversationViewDTO> conversations,
                                                   List<UUID> conversationIds,
                                                   Long senderId){
        //Fetch last messages
        Map<UUID, LastMessageDTO> lastMessages = getLastMessages(conversationIds);

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

    private Map<UUID, LastMessageDTO> getLastMessages(List<UUID> conversationIds){

        Map<UUID, LastMessageDTO> lastMessages = new HashMap<>();
        List<Message> messages = dataService.getLastMessages(conversationIds);
        
        conversationIds
        	.forEach(id -> {

	            //Filter own conversation last message
	            Message conversationMessage = messages
	                    .stream()
	                    .filter(m -> m.getConversation().getId().equals(id))
	                    .findFirst().orElse(null);
	
	            if(conversationMessage!=null) {
	                lastMessages.put(id, mapper.map(conversationMessage, LastMessageDTO.class));
	            }
        	});
    	return lastMessages;
    }

    
    private Map<UUID, Long> getUnreadMessages(Long participantId, List<UUID> conversationIds){

        Map<UUID, Long> unreadMessages = new HashMap<>();
        List<UnreadMessagesDTO> counts = dataService.getUnreadMessages(participantId, conversationIds);

        if(!counts.isEmpty()){

        	counts
        		.forEach(unreadMessage -> {
        			
        			Long count = unreadMessage.getCount();
	        		unreadMessages.put(
	        				unreadMessage.getConversationId(), 
	        				 count != null ? count : 0);
        		});
        }
        return unreadMessages;
    }


    
    

    public MessageService(MessageDataService dataService, @Lazy ConversationService conversationService, SimpMessagingTemplate webSocketTemplate, ModelMapper mapper) {
        this.dataService = dataService;
    	this.conversationService = conversationService;
        this.webSocketTemplate = webSocketTemplate;
        this.mapper = mapper;
    }

    private final MessageDataService dataService;
    private final ConversationService conversationService;
    private final SimpMessagingTemplate webSocketTemplate;
    private final ModelMapper mapper;
}