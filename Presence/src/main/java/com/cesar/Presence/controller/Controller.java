package com.cesar.Presence.controller;

import com.cesar.Presence.service.PresenceService;
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
@RequestMapping("/presences")
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
    public ResponseEntity<?> getStatuses(List<Long> userIds){
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(presenceService.getPresences(userIds));
    }

    @Autowired
    private PresenceService presenceService;
    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
}