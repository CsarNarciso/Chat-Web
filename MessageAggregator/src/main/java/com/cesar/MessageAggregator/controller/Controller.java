package com.cesar.MessageAggregator.controller;

import com.cesar.MessageAggregator.service.ComposedMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/composedMessages")
public class Controller {

    @GetMapping("/{conversationId}")
    public ResponseEntity<?> getConversationMessages(@PathVariable Long conversationId){
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(service.getConversationMessages(conversationId));
    }

    @Autowired
    private ComposedMessageService service;
}