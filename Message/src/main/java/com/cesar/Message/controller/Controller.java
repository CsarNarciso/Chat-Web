package com.cesar.Message.controller;

import com.cesar.Message.dto.MessageDTO;
import com.cesar.Message.dto.NewConversationFirstMessageDTO;
import com.cesar.Message.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/messages")
public class Controller {

    @MessageMapping("/sendFirst")
    public void sendNewConversationFirstMessage(NewConversationFirstMessageDTO message) {
        Long newConversationId = service.storeNewConversationFirstMessage(message);
        String sendingDestination =
                "/topic/" + newConversationId + "/queue/conversation";
        simp.convertAndSend(sendingDestination, message);
    }

    @MessageMapping("/send")
    public void send(MessageDTO message) {
        Long conversationId = service.store(message);
        String sendingDestination =
                "/topic/" + conversationId + "/queue/conversation";
        simp.convertAndSend(sendingDestination, message);
    }

    @GetMapping("/{conversationId}")
    public ResponseEntity<?> getConversationMessages(@PathVariable Long conversationId){
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(service.getConversationMessages(conversationId));
    }

    @Autowired
    private MessageService service;
    @Autowired
    private SimpMessagingTemplate simp;
}