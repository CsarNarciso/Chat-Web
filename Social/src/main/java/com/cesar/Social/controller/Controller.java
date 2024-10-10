package com.cesar.Social.controller;

import com.cesar.Social.service.PresenceService;
import com.cesar.Social.service.RelationshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/social")
public class Controller {

    @PostMapping("/connect/{userId}")
    public void connect(@PathVariable Long userId) {
        presenceService.connect(userId);
    }

    @PutMapping("/disconnect/{userId}")
    public void disconnect(@PathVariable Long userId) {
        presenceService.disconnect(userId);
        //Wait for reconnection...,
        scheduler.schedule(() -> {
            //Handle if user remains disconnected...
            presenceService.removeOffline(userId);
        }, 5, TimeUnit.SECONDS);
    }

    @GetMapping
    public ResponseEntity<?> getStatuses(List<Long> usersIds){
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(presenceService.getStatuses(usersIds));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserRelationships(@PathVariable Long userId){
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(relationshipService.getByUserId(userId));
    }

    @Autowired
    private PresenceService presenceService;
    @Autowired
    private RelationshipService relationshipService;
    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
}