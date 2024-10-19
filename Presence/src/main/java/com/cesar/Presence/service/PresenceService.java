package com.cesar.Presence.service;

import com.cesar.Presence.dto.UserPresenceDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
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
            //OR FIRST INVALIDATE, THEN SET AGAIN
            //THEN, CHECK HOW WE CAN RETRIEVE ALL CACHES IN A DTO OBJECT
            redisTemplate.opsForValue().set(userPresenceKey, presence);
        }
        else {

            //Connect
            redisTemplate.opsForValue().set(userPresenceKey, presence);
        }

        //Event Publisher - User Online
        //Data for: presence data for Social Service
        kafkaTemplate.send("PresenceUpdated", presence);
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
        //Data for: presence data for Social Service
        kafkaTemplate.send("PresenceUpdated", presence);
    }


    public void removeOffline(Long userId){

        String userPresenceKey = generateUserPresenceKey(userId);
        UserPresenceDTO presence = (UserPresenceDTO) redisTemplate.opsForValue().get(userPresenceKey);

        //If still Offline...
        if (presence.getStatus().equals("OFFLINE")) {

            //Forgot it

            //Remove it from Cache
            redisTemplate.delete(userPresenceKey);

            //Event Publisher - Presence Forgotten
            //Data for: user id for Social Service
            kafkaTemplate.send("PresenceForgotten", userId);
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




    @KafkaListener(topics = "UserDeleted", groupId = "${spring.kafka.consumer.group-id}")
    public void onUserDeleted(Long id){

        //Forgot user presence
        String userPresenceKey = generateUserPresenceKey(id);
        redisTemplate.delete(userPresenceKey);
    }





    private String generateUserPresenceKey(Long id){
        return String.format("%s", id);
    }

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;
}