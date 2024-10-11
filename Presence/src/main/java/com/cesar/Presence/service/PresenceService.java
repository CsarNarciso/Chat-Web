package com.cesar.Presence.service;

import com.cesar.Presence.dto.PresenceStatusDTO;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PresenceService {

    public void connect(Long userId) {
        PresenceStatusDTO presence = PresenceStatusDTO
                .builder()
                .userId(userId)
                .status("ONLINE")
                .build();
        //If user already exists in list (either online or not)...
        if(presences.containsKey(userId)){
            //Reconnect
            presences.replace(userId, presence);
        }
        else {
            //Connect
            presences.put(userId, presence);
        }
        //Event Publisher - User Online
        //when user connects
        //Data for: user presence data for Social Service
    }
    public void disconnect(Long userId) {
        //Mark as Offline,
        PresenceStatusDTO presence = PresenceStatusDTO
                .builder()
                .userId(userId)
                .status("OFFLINE")
                .lastSeen(LocalDateTime.now())
                .build();
        presences.replace(userId, presence);

        //Event Publisher - User Offline
        //when user starts disconnection (is registered as offline)
        //Data for: user presence data for Social Service
    }
    public void removeOffline(Long userId){
        if (presences.get(userId).getStatus().equals("OFFLINE")) {
            presences.remove(userId);
            //Event Publisher - User disconnected
            //when user pass certain time offline
            //Data for: user presence data for Social Service
            PresenceStatusDTO presence = PresenceStatusDTO
                    .builder()
                    .userId(userId)
                    .build();
        }
    }
    public List<PresenceStatusDTO> getStatuses(List<Long> usersIds){
        List<PresenceStatusDTO> statuses = new ArrayList<>();
        usersIds
                .forEach(userId -> {
                    statuses.add(statuses.get(userId.intValue()));
                });
        return statuses;
    }

    private final Map<Long, PresenceStatusDTO> presences = new ConcurrentHashMap<>();
}