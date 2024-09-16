package com.cesar.PresenceService.service;

import com.cesar.PresenceService.dto.UserDTO;
import com.cesar.PresenceService.model.OnlineUser;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PresenceService {

    public List<OnlineUser> connect(UserDTO user) {
        OnlineUser onlineUser = mapper.map(user, OnlineUser.class);
        onlineUser.setStatus("Online");
        users.put(user.getId(), onlineUser);
        return users.values().stream().toList();
    }

    public OnlineUser disconnect(Long userId) {

        //Mark as offline,
        OnlineUser user = users.get(userId);
        user.setStatus("Offline");
        user.setDisconnectionHour(System.currentTimeMillis());
        users.replace(userId, user);

        //but wait for reconnection...,
        new Timer().schedule(new TimerTask() {

            @Override
            public void run() {

                //and if user remains disconnected...
                if ( user.getStatus().equals("Offline") ) {

                    //Remove it.
                    users.remove(userId);
                    //Send update alert to all users.
                    simp.convertAndSend("/topic/updateOnlineUsers", userId);
                }
                //Stop timer.
                cancel();
            }
        }, 0, 5000);
        return user;
    }

    @Autowired
    private ModelMapper mapper;
    @Autowired
    private SimpMessagingTemplate simp;
    private final Map<Long, OnlineUser> users = new ConcurrentHashMap<>();
}