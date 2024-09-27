package com.cesar.Conversation.controller;

import com.cesar.Conversation.dto.FirstMessageDTO;
import com.cesar.Conversation.service.ConversationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/conversations")
public class Controller {

    @PostMapping
    public void onFirstInteraction(@RequestBody FirstMessageDTO firstMessage){
        service.create(firstMessage);
    }
    @GetMapping
    public ResponseEntity<?> onLoadConversations(@RequestBody List<Long> conversationsIds){
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(service.loadConversations(conversationsIds));
    }
    @PostMapping("/cleanUnreadMessages/{userId}")
    public void onCleanUnreadMessages(Long userId){
        service.cleanUnreadMessages(userId);
    }
    @Autowired
    private ConversationService service;
}