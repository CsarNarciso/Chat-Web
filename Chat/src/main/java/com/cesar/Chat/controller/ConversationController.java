package com.cesar.Chat.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.cesar.Chat.dto.ConversationViewDTO;
import com.cesar.Chat.dto.MessageForInitDTO;
import com.cesar.Chat.service.ConversationService;

@RestController
@RequestMapping("/conversations")
public class ConversationController {
	
    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public void onFirstInteraction(@RequestBody MessageForInitDTO initMessage){
        service.create(null, initMessage);
    }

    @GetMapping(value = "/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> onLoad(@PathVariable Long userId){
    	List<ConversationViewDTO> conversations = service.load(userId);
    	return conversations!=null 
    			? ResponseEntity
    					.status(HttpStatus.OK)
    					.body(conversations)
    			: ResponseEntity.noContent().build();
    }

    @DeleteMapping(value = "/{conversationId}/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> onDelete(@PathVariable UUID conversationId, @PathVariable Long userId){
    	
    	Object permanently = service.delete(conversationId, userId); 
    	HttpStatus status = HttpStatus.NOT_FOUND;
    	
    	if (permanently!=null && !(boolean)permanently) {
    		status = HttpStatus.NO_CONTENT;
    	}
    	return ResponseEntity.status(status).build();
    }



    public ConversationController(ConversationService service){
    	this.service = service;
    }

    private final ConversationService service;
}
