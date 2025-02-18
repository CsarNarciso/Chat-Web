package com.cesar.User.controller;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cesar.User.service.PresenceService;

@RestController
@RequestMapping("/presences")
public class PresenceController {

    @PostMapping("/connect/{userId}")
    public void connect(@PathVariable Long userId) {
        presenceService.connect(userId);
    }

    @PutMapping("/disconnect/{userId}")
    public void disconnect(@PathVariable Long userId) {
        presenceService.disconnect(userId);
        //Wait for reconnection...,
        scheduler.schedule(() -> {
            //And if user remains disconnected
            presenceService.removeOffline(userId); //Forget it
        }, 5, TimeUnit.SECONDS);
    }

    @GetMapping
    public ResponseEntity<?> getStatuses(List<Long> userIds){
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(presenceService.getPresences(userIds));
    }




    public PresenceController(PresenceService presenceService){
    	this.presenceService = presenceService;
    }

    private final PresenceService presenceService;
    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
}