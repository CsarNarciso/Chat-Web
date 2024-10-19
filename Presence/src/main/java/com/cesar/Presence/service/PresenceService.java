package com.cesar.Presence.service;

import com.cesar.Presence.dto.UserPresenceDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@EnableCaching
@Service
public class PresenceService {

    public void connect(Long userId) {

        String userPresenceKey = generateUserPresenceKey(userId);
        UserPresenceDTO presence = UserPresenceDTO
                .builder()
                .id(userId)
                .status("ONLINE")
                .build();

        //If presence already exists (either online or not)...
        if(redisTemplate.opsForValue().get(userPresenceKey)!=null){

            //Reconnect - CHECK THIS
            //IN REDIS, THIS IS NOT REPLACING, WE NEED TO USE HASHES...
            //THEN, CHECK HOW WE CAN RETRIEVE ALL CACHES IN A DTO OBJECT
            redisTemplate.opsForValue().set(userPresenceKey, presence);
        }
        else {

            //Connect
            redisTemplate.opsForValue().set(userPresenceKey, presence);
        }
        //Event Publisher - User Online
        //when user connects
        //Data for: user presence data for Social Service
    }


    public void disconnect(Long userId) {

        //Mark as Offline
        String userPresenceKey = generateUserPresenceKey(userId);
        UserPresenceDTO presence = UserPresenceDTO
                .builder()
                .id(userId)
                .status("OFFLINE")
                .lastSeen(LocalDateTime.now())
                .build();

        redisTemplate.opsForValue().set(userPresenceKey, presence);

        //Event Publisher - User Offline
        //when user starts disconnection (is registered as offline)
        //Data for: user presence data for Social Service
    }


    public void removeOffline(Long userId){

        String userPresenceKey = generateUserPresenceKey(userId);
        UserPresenceDTO presence = (UserPresenceDTO) redisTemplate.opsForValue().get(userPresenceKey);

        //If still Offline...
        if (presence.getStatus().equals("OFFLINE")) {

            //Remove it from Cache
            redisTemplate.delete(userPresenceKey);
            //Event Publisher - User disconnected
            //when user pass certain time offline
            //Data for: user presence data for Social Service
        }
    }


    public List<UserPresenceDTO> getPresences(List<Long> userIds){

        //Fetch from Cache
        Set<String> userPresenceKeys = userIds
                .stream()
                .map(this::generateUserPresenceKey)
                .collect(Collectors.toSet());

        return Objects.requireNonNull(
                redisTemplate.opsForValue().multiGet(userPresenceKeys))
                .stream()
                .map(p -> (UserPresenceDTO) p)
                .toList();
    }





    private String generateUserPresenceKey(Long id){
        return String.format("%s", id);
    }

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
}