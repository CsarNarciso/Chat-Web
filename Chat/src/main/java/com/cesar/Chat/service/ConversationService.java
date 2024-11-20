package com.cesar.Chat.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import org.modelmapper.ModelMapper;
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
import com.cesar.Chat.dto.MessageForSendDTO;
import com.cesar.Chat.entity.Conversation;
import com.cesar.Chat.entity.Message;

@Service
public class ConversationService {
	
	public void create(ConversationDTO conversation, MessageForInitDTO firstInteractionMessage) {
	
		List<Long> createFor = new ArrayList<>();
		Conversation savedEntity = new Conversation();
		
	    //Get message participant IDs
		Long senderId = firstInteractionMessage.getSenderId();
		Long recipientId = firstInteractionMessage.getRecipientId();
	    List<Long> userIds = new ArrayList<>(Stream.of(
	    		senderId,
	    		recipientId)
			.toList());
	
	    //Look for an existent conversation between both users
	    Conversation existentConversation = dataAccessService.matchByUserIds(userIds, userIds.size());
	
	    //If don't exists
	    if(existentConversation==null){
	
	        //Create
	        createFor = userIds;
	        
	        Conversation newConversation = new Conversation();
	        newConversation.setId(UUID.randomUUID());
	        Collections.sort(userIds);
	        newConversation.setParticipants(userIds);
	        newConversation.setCreatedAt(LocalDateTime.now());
	        newConversation.setRecreateFor(Collections.emptyList());
	        
	        //Message reference
	        Message message = messageService.createEntityOnSendRequest(mapper.map(firstInteractionMessage, MessageForSendDTO.class));
	        newConversation.addMessage(message);
	        
	        //Save (along with message)
	        savedEntity = dataAccessService.save(newConversation, senderId, recipientId);
	        conversation = mapToDTO(savedEntity);
	    }
	    else{
	
	        //Recreate
	        createFor = existentConversation.getRecreateFor();
	        existentConversation.setRecreateFor(Collections.emptyList());               
	        savedEntity = dataAccessService.save(existentConversation, senderId, recipientId);
	        conversation = mapToDTO(savedEntity);
	    }
	
		//For everyone involved in the creation/recreation
		for(Long participantId : createFor){
		        	
			//Compose custom conversation view: recipient presence/details, last message data and unread message count
		    ConversationViewDTO conversationView = 
		    		composeConversationsData(Stream.of(conversation).toList(), participantId).getFirst();
			
		    //Send
		    webSocketTemplate.convertAndSendToUser(
		            participantId.toString(),
		            "/user/reply/createConversation",
		            conversationView);
		};
		
	    //Update conversation recreateForSomeone on client
	    webSocketTemplate.convertAndSend(conversation.getId().toString() + "/updateRecreateForSomeone", false);
		
		//Event publisher - ConversationCreated
		kafkaTemplate.send("ConversationCreated", ConversationCreatedDTO
		    		.builder()
		            .id(conversation.getId())
		            .createFor(createFor)
		            .build());
	}
	
	
	public List<ConversationViewDTO> load(Long userId){
		List<ConversationDTO> conversations = new ArrayList<>();
		conversations = dataAccessService.getAllByUserId(userId);
		return (!conversations.isEmpty()) 
				? composeConversationsData(conversations, userId) //Compose views
				: null;
	}
	
	
	
	public Object delete(UUID conversationId, Long participantId){
	
		//Look for conversation
		Conversation entity = dataAccessService.getById(conversationId);
		boolean permanently = false;
		
		//If exists...
		if(entity!=null){
		
		    //Get conversation participant IDs
		    List<Long> participantsIds = entity.getParticipants();
		
		    //Add userId to conversation recreateFor list
		    List<Long> recreateFor = entity.getRecreateFor();
		    if(!recreateFor.contains(participantId)) {
		    	recreateFor.add(participantId);
		    	Collections.sort(recreateFor);
		    }
		    
		    //If participantIds matches recreateFor...
		    if(recreateFor.equals(participantsIds)){
		
		        //Deletion is permanently
		        permanently = true;
		        dataAccessService.delete(conversationId, participantId);
		    }
		    //If not,
		    else{
		        //Deletion is local
		
		        //Update recreateFor list
		    	entity.setRecreateFor(recreateFor);
		    	dataAccessService.save(entity, participantId, participantsIds
		    			.stream()
		    			.filter(id->!id.equals(participantId))
		    			.findFirst().orElse(null));
		    	
		    	//Update conversation recreateForSomeone on client
			    webSocketTemplate.convertAndSend(conversationId.toString() + "/updateRecreateForSomeone", true);
		    }
		    
		    //Ask Message service to delete deleted conversation's messages/unread counts
		    messageService.onConversationDeleted(conversationId, participantId);
		
		    //Event publisher - ConversationDeleted
		    kafkaTemplate.send("ConversationDeleted", ConversationDeletedDTO
		            .builder()
		            .id(conversationId)
		            .participantId(participantId)
		            .permanently(permanently)
		            .build());
		    return permanently;
		}
		return null;
	}
	
	
	private List<ConversationViewDTO> composeConversationsData(List<ConversationDTO> conversations,
	                                                     Long conversationViewOwnerId){
		List<Long> recipientIds = new ArrayList<>();
		List<UUID> conversationIds = mapToIds(conversations);
		List<ConversationViewDTO> conversationViews = mapToViewDTOs(conversations);
		
		//For each conversation
		for (int i = 0; i < conversations.size(); i++){
		
			ConversationDTO conversation = conversations.get(i);
			
		    //Get recipient reference (conversation face for sender)
		    Long recipientId = conversation.getParticipants()
		            .stream()
		            .filter(id -> !id.equals(conversationViewOwnerId))
		            .findFirst().orElse(null);
		    recipientIds.add(recipientId);
		    
		    //And compose as part of conversation view
		    conversationViews.get(i).setRecipient(
		            ConversationRecipientDTO
		                    .builder()
		                    .userId(recipientId)
		                    .build());
		    
		    //Set recreate for someone
		    if(!conversation.getRecreateFor().isEmpty()) {
		    	conversationViews.get(i).setRecreateForSomeone(true);
		    }
		}
		//Inject recipients' user details
		userService.injectConversationsParticipantsDetails(conversationViews, recipientIds);
		
		//, presence statuses
		presenceService.injectConversationsParticipantsStatuses(conversationViews, recipientIds);
		
		//And view owner last messages and unread message counts
		messageService.injectConversationsMessagesDetails(conversationViews, conversationIds, conversationViewOwnerId);
		
		return conversationViews;
	}
	
	
	@KafkaListener(topics = "UserDeleted", groupId = "${spring.kafka.consumer.group-id}")
	public void onUserDeleted(Long id){
		//Ask message service to delete messages and unread counts
		messageService.onUserDeleted(id);
	}
	
	
	
	
	
	
	private ConversationDTO mapToDTO(Conversation conversation){
		return mapper.map(conversation, ConversationDTO.class);
	}
	
	private List<ConversationViewDTO> mapToViewDTOs(List<ConversationDTO> conversations){
		return conversations
		        .stream()
		        .map(c -> mapper.map(c, ConversationViewDTO.class))
		        .toList();
	}
	private List<UUID> mapToIds(List<ConversationDTO> conversations){
		return conversations
		        .stream()
		        .map(ConversationDTO::getId)
		        .toList();
	}
	
	
	
	
	
	
	public ConversationService(ConversationDataService dataService, MessageService messageService, PresenceService presenceService, UserService userService, KafkaTemplate<String, Object> kafkaTemplate, SimpMessagingTemplate webSocketTemplate, ModelMapper mapper) {
		this.dataService = dataService;
		this.messageService = messageService;
		this.presenceService = presenceService;
		this.userService = userService;
		this.kafkaTemplate = kafkaTemplate;
		this.webSocketTemplate = webSocketTemplate;
		this.mapper = mapper;
	}
	
	private final ConversationDataService dataService;
	private final MessageService messageService;
	private final PresenceService presenceService;
	private final UserService userService;
	private final KafkaTemplate<String, Object> kafkaTemplate;
	private final SimpMessagingTemplate webSocketTemplate;
	private final ModelMapper mapper;
}