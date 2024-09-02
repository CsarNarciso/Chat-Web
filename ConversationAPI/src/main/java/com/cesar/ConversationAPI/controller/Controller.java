package com.cesar.ConversationAPI.controller;

import com.cesar.ConversationAPI.dto.ConversationDTO;
import com.cesar.ConversationAPI.entity.Conversation;
import com.cesar.ConversationAPI.service.ConversationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/conversations.api")
public class Controller {

    @PostMapping("/v1")
    public ResponseEntity<?> create(@RequestBody Conversation conversation) {

        ConversationDTO savedConversation = service.create(conversation);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(savedConversation);
    }

    @GetMapping("/{id}/v1")
    public ResponseEntity<?> getById(@PathVariable Long id) {

        ConversationDTO conversation = service.getById(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(conversation);
    }

    @GetMapping("/{senderId}/v1")
    public ResponseEntity<?> getBySenderId(@PathVariable Long senderId) {

        List<ConversationDTO> conversations = service.getBySenderId(senderId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(conversations);
    }

    @GetMapping("/{recipientId}/v1")
    public ResponseEntity<?> getByRecipientId(@PathVariable Long recipientId) {

        List<ConversationDTO> conversations = service.getBySenderId(recipientId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(conversations);
    }

    @Autowired
    private ConversationService service;
}
