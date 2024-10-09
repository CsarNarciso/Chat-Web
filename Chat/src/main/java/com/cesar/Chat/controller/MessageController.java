package com.cesar.Chat.controller;

import com.cesar.Chat.dto.MessageForSendDTO;
import com.cesar.Chat.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/messages")
public class MessageController {

    @PostMapping
    public void onSend(@RequestBody MessageForSendDTO message){
        service.send(message);
    }

    @GetMapping("/{conversationId}")
    public ResponseEntity<?> onLoadMessages(@PathVariable Long conversationId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(service.loadConversationMessages(conversationId));
    }

    @Autowired
    private MessageService service;
}