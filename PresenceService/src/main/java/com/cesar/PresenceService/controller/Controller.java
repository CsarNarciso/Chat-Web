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

        List<OnlineUser> onlineUsers = service.connect(user);
        //sent new online user to all users
        simp.convertAndSend("/user/" + user.getId() + "/queue/getOnlineUsers", onlineUsers);
        simp.convertAndSend("/topic/updateOnlineUsers", user);
        //Send actual online users list to recent connected user.
        simp.convertAndSend("/user/" + user.getId() + "/queue/getOnlineUsers", onlineUsers);
    }

    @MessageMapping("/disconnect")
    public void disconnect(Long userId) {

        OnlineUser user = service.disconnect(userId);
        //Send update (recent disconnected user) to all users.
        simp.convertAndSend("/topic/updateOnlineUsers", user);

        //but wait for reconnection...,
        scheduler.schedule(() -> {

            //and if user remains disconnected...
            if (service.removeOffline(userId)) {
                //Send update alert to all users.
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