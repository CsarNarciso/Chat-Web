package com.cesar.ConversationAPI.controller;

import com.cesar.ConversationAPI.dto.ConversationDTO;
import com.cesar.ConversationAPI.dto.CreationRequestDTO;
import com.cesar.ConversationAPI.service.ConversationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/conversations.api")
public class Controller {

    @PostMapping("/v1")
    public ResponseEntity<?> start(@RequestBody CreationRequestDTO creationRequest) {

        ConversationDTO conversationData = service.createConversation(creationRequest);
        ConversationDTO senderConversationData = conversationData;

        //Provide conversation data for each participant
        conversationData.getParticipants().stream()
            .forEach(participant -> {

                conversationData.getParticipants().stream().filter(participantWhoReceives -> participantWhoReceives.equals(participant));

                if(participant.isSender()){
                    //Retrieve for sender
                    senderConversationData.setParticipants(conversationData.getParticipants());
                } else{
                    //Send for each recipient
                    simp.convertAndSendToUser(participant.getId().toString(), "/createConversation", conversationData);
                }
            });
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(senderConversationData);
    }

    @Autowired
    private ConversationService service;
    @Autowired
    private SimpMessagingTemplate simp;
}