package com.cesar.Presence.service;

import com.cesar.Presence.model.OnlineUser;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PresenceService {

    public void connect(Long userId) {
        OnlineUser onlineUser = OnlineUser
                .builder()
                .userId(userId)
                .status("ONLINE")
                .build();
        //If user already exists in list (either online or not)...
        if(statuses.containsKey(userId)){
            //Reconnect
            statuses.replace(userId, onlineUser);
        }
        else {
            //Connect
            statuses.put(userId, onlineUser);
        }
        //Event Publisher - User Online
        //when user connects
        //Data for: userId, status data for WS server
    }
    public void disconnect(Long userId) {
        //Mark as Offline,
        OnlineUser offlineUser = OnlineUser
                .builder()
                .userId(userId)
                .status("OFFLINE")
                .lastSeen(LocalDateTime.now())
                .build();
        statuses.replace(userId, offlineUser);

        //Event Publisher - User Offline
        //when user starts disconnection (is registered as offline)
        //Data for: userId, status data for WS server
    }
    public void removeOffline(Long userId){
        if (statuses.get(userId).getStatus().equals("OFFLINE")) {
            statuses.remove(userId);
            //Event Publisher - User disconnected
            //when user pass certain time offline
            //Data for: userId, status data for WS server
            OnlineUser disconnectedUser = OnlineUser
                    .builder()
                    .userId(userId)
                    .build();
        }
    }
    public List<OnlineUser> getStatuses(List<Long> usersIds){
        List<OnlineUser> statuses = new ArrayList<>();
        usersIds
                .forEach(userId -> {
                    statuses.add(statuses.get(userId.intValue()));
                });
        return statuses;
    }

    //Presence statuses need to be stored in CACHE,
    //for the moment, store locally
    private final Map<Long, OnlineUser> statuses = new ConcurrentHashMap<>();
}