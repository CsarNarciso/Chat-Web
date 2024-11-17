package com.cesar.Chat.controller;

import com.cesar.Chat.dto.MessageForSendDTO;
import com.cesar.Chat.service.MessageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/messages")
public class MessageController {


    @PostMapping
    public void onSend(@RequestBody MessageForSendDTO message){
        service.send(message);
    }

    @GetMapping(value = "/{conversationId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> onLoad(@PathVariable String conversationId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(service.loadConversationMessages(UUID.fromString(conversationId)));
    }

    @PutMapping(value = "/clean.unread/{conversationId}/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public void onCleanUnread(@PathVariable UUID conversationId, @PathVariable Long userId){
    	service.cleanConversationUnreadMessages(conversationId, userId);
    }



    public MessageController(MessageService service) {
        this.service = service;
    }

    private final MessageService service;
}
