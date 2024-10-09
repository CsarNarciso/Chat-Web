package com.cesar.Chat.controller;

import com.cesar.Chat.dto.MessageForInitDTO;
import com.cesar.Chat.service.ConversationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/conversations")
public class ConversationController {

    @PostMapping
    public void onFirstInteraction(@RequestBody MessageForInitDTO initMessage){
        service.create(null, initMessage);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> onLoad(@PathVariable Long userId){
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(service.load(userId));
    }

    @DeleteMapping("/{conversationId}/{userId}")
    public ResponseEntity<?> onDelete(@PathVariable Long conversationId, @PathVariable Long userId){
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(service.delete(conversationId, userId));
    }

    @Autowired
    private ConversationService service;
}