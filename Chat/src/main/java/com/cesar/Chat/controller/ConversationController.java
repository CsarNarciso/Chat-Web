package com.cesar.Chat.controller;

import com.cesar.Chat.dto.MessageForInitDTO;
import com.cesar.Chat.service.ConversationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/conversations")
public class ConversationController {


    @PostMapping
    public void onFirstInteraction(@RequestBody MessageForInitDTO initMessage){
        service.create(null, initMessage);
    }

    @GetMapping(value = "/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> onLoad(@PathVariable Long userId){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(service.load(userId));
    }

    @DeleteMapping(value = "/{conversationId}/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> onDelete(@PathVariable UUID conversationId, @PathVariable Long userId){
    	return service.delete(conversationId, userId) 
    			? ResponseEntity.status(HttpStatus.NOT_FOUND).build() 
    			: ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }



    public ConversationController(ConversationService service){
    	this.service = service;
    }

    private final ConversationService service;
}
