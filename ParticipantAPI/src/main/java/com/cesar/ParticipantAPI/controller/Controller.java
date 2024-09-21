package com.cesar.ParticipantAPI.controller;

import com.cesar.ParticipantAPI.service.ParticipantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/participants.api")
public class Controller {

    @PostMapping
    public ResponseEntity<?> buildParticipants(List<Long> participantsIds){
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(service.buildParticipants(participantsIds));
    }

    @Autowired
    private ParticipantService service;
}