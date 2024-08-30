package com.cesar.MessageAPI.controller;

import com.cesar.MessageAPI.dto.MessageDTO;
import com.cesar.MessageAPI.entity.Message;
import com.cesar.MessageAPI.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/messages.api.v1")
public class Controller {

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Message message){

        MessageDTO savedMessage = service.create(message);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(savedMessage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {

        MessageDTO message = service.getById(id);

        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(message);
    }

    @GetMapping("/{senderId}")
    public ResponseEntity<?> getBySenderId(@PathVariable Long senderId) {

        List<MessageDTO> messages = service.getBySenderId(senderId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(messages);
    }

    @GetMapping("/{recipientId}")
    public ResponseEntity<?> getByRecipientId(@PathVariable Long recipientId) {

        List<MessageDTO> messages = service.getByRecipientId(recipientId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(messages);
    }

    @Autowired
    private MessageService service;
}
