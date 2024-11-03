package com.cesar.Presence.service;

import com.cesar.Presence.dto.UserPresenceDTO;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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

            //Reconnect
            redisTemplate.opsForValue().set(userPresenceKey, presence);
        }
        else {

            //Connect
            redisTemplate.opsForValue().set(userPresenceKey, presence);
        }

        //Event Publisher - User Online
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
        kafkaTemplate.send("PresenceUpdated", presence);
    }


    public void removeOffline(Long userId){

        String userPresenceKey = generateUserPresenceKey(userId);
        UserPresenceDTO presence = redisTemplate.opsForValue().get(userPresenceKey);

        //If still Offline...
        if (presence.getStatus().equals("OFFLINE")) {

            //Forgot it

            //Remove it from Cache
            redisTemplate.delete(userPresenceKey);

            //Event Publisher - Presence Forgotten
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
                redisTemplate.opsForValue().multiGet(userPresenceKeys));
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




    public PresenceService(RedisTemplate<String, UserPresenceDTO> redisTemplate, KafkaTemplate<String, Object> kafkaTemplate) {
        this.redisTemplate = redisTemplate;
        this.kafkaTemplate = kafkaTemplate;
    }

    private final RedisTemplate<String, UserPresenceDTO> redisTemplate;
    private final KafkaTemplate<String, Object> kafkaTemplate;
}