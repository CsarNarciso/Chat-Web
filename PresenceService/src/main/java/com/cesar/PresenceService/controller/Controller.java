package com.cesar.PresenceService.controller;

import com.cesar.PresenceService.dto.UserDTO;
import com.cesar.PresenceService.model.OnlineUser;
import com.cesar.PresenceService.service.PresenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/onlineUsers.api")
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
    }

    @Autowired
    private PresenceService service;
    @Autowired
    private SimpMessagingTemplate simp;
}
