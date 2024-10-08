package com.cesar.Message.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/messages")
public class Controller {

    @PostMapping
    public void onSend(CreationRequestDTO message) {
        service.send(message);
    }
    @GetMapping("/{conversationId}")
    public ResponseEntity<?> onLoadConversationMessages(@PathVariable Long conversationId){
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(service.loadConversationMessages(conversationId));
    }
    @Autowired
    private MessageService service;
}