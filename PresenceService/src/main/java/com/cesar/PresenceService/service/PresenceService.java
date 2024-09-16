package com.cesar.PresenceService.service;

import com.cesar.PresenceService.dto.UserDTO;
import com.cesar.PresenceService.model.OnlineUser;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PresenceService {

    public OnlineUser connect(UserDTO user) {
        OnlineUser onlineUser = mapper.map(user, OnlineUser.class);
        onlineUser.setStatus("Online");
        //If user already exists...
        return users.containsKey(user.getId()) ?
                users.replace(user.getId(), onlineUser) : //, reconnect
                users.put(user.getId(), onlineUser); //connect
    }

    public OnlineUser reconnect(UserDTO user) {
        if(users.containsKey(userId)){
            OnlineUser onlineUser = users.get(userId);
            onlineUser.setStatus("Online");
            users.replace(user.getId(), onlineUser);
            return onlineUser;
        }
        return null;
    }

    public OnlineUser disconnect(Long userId) {
        //Mark as offline,
        OnlineUser user = users.get(userId);
        user.setStatus("Offline");
        user.setDisconnectionHour(System.currentTimeMillis());
        users.replace(userId, user);
        return user;
    }

    public boolean removeOffline(Long userId){
        OnlineUser user = users.get(userId);
        if (user.getStatus().equals("Offline")) {
            users.remove(userId);
            return true;
        }
        return false;
    }

    public List<OnlineUser> getUsers(){
        return users.values().stream().toList();
    }

    @Autowired
    private ModelMapper mapper;
    private final Map<Long, OnlineUser> users = new ConcurrentHashMap<>();
}