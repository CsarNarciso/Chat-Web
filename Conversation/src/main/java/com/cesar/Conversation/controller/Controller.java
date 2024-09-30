package com.cesar.Conversation.controller;

import com.cesar.Conversation.dto.CreationRequestDTO;
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
    public ResponseEntity<?> onCreate(@RequestBody CreationRequestDTO creationRequest){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(service.create(creationRequest));
    }
    @PutMapping("/recreate/{conversationId}")
    public ResponseEntity<?> onRecreate(@PathVariable Long conversationId){
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(service.recreate(conversationId));
    }
    @GetMapping
    public ResponseEntity<?> onLoadConversations(@RequestBody List<Long> conversationsIds){
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(service.loadConversations(conversationsIds));
    }
    @PostMapping("/closeConversation/{participantId}")
    public void onCloseConversation(Long participantId){
        service.closeConversation(participantId);
    }
    @Autowired
    private ConversationService service;
}