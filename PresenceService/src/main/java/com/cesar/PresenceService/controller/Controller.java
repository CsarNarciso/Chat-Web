package com.cesar.PresenceService.controller;

import com.cesar.PresenceService.dto.UserDTO;
import com.cesar.PresenceService.model.OnlineUser;
import com.cesar.PresenceService.service.PresenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/usersPresence")
public class Controller {

    @MessageMapping("/connect")
    public void connect(UserDTO user) {

        //Send connected/reconnected user to all users
        OnlineUser onlineUser = service.connect(user);
        simp.convertAndSend("/topic/updateOnlineUsers", onlineUser);

        //Send actual users list to recent connected user.
        List<OnlineUser> users = service.getUsers();
        simp.convertAndSend("/user/" + user.getId() + "/queue/getOnlineUsers", users);
    }

    @MessageMapping("/disconnect")
    public void disconnect(Long userId) {

        //Send disconnected user to all users.
        OnlineUser offlineUser = service.disconnect(userId);
        simp.convertAndSend("/topic/updateOnlineUsers", offlineUser);

        //and wait for reconnection...,
        scheduler.schedule(() -> {

            //if user remains disconnected...
            if (service.removeOffline(userId)) {
                //remove it, send update alert to all users.
                simp.convertAndSend("/topic/updateOnlineUsers", userId);
            }
        }, 5, TimeUnit.SECONDS);
    }

    @Autowired
    private PresenceService service;
    @Autowired
    private SimpMessagingTemplate simp;
    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
}