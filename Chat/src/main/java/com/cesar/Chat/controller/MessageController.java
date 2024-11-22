package com.cesar.Chat.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.cesar.Chat.dto.MessageDTO;
import com.cesar.Chat.dto.MessageForSendDTO;
import com.cesar.Chat.service.MessageService;

@RestController
@RequestMapping("/messages")
public class MessageController {

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public void onSend(@RequestBody MessageForSendDTO message){
        service.send(message);
    }

    @GetMapping(value = "/{conversationId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> onLoad(@PathVariable UUID conversationId) {
        List<MessageDTO> messages = service.loadConversationMessages(conversationId);
    	return !messages.isEmpty() 
    			? ResponseEntity
                	.status(HttpStatus.OK)
                	.body(messages)
                : ResponseEntity.noContent().build();
    }

    @PutMapping(value = "/clean.unread/{conversationId}/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void onCleanUnread(@PathVariable UUID conversationId, @PathVariable Long userId){
    	service.cleanUnreadMessages(conversationId, userId);
    }



    public MessageController(MessageService service) {
        this.service = service;
    }

    private final MessageService service;
}
