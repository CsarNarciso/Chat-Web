//package com.cesar.User.service;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Objects;
//import java.util.Set;
//import java.util.stream.Collectors;
//
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.stereotype.Service;
//
//import com.cesar.User.dto.PresenceDTO;
//
//@Service
//public class PresenceService {
//
//
//    public void connect(Long userId) {
//
//		String userPresenceKey = generateUserPresenceKey(userId);
//		PresenceDTO existentPresence = redisTemplate.opsForValue().get(userPresenceKey);
//		
//		//If presence already exists as Offline (reconnection)or doesn't exist yet (connection)...
//		if(existentPresence != null && existentPresence.getStatus().equals("OFFLINE")){
//			
//			//Perform connection action
//			PresenceDTO presence = PresenceDTO
//                .builder()
//                .id(userId)
//                .status("ONLINE")
//                .build();
//			
//			redisTemplate.opsForValue().set(userPresenceKey, presence);
//			
//			//Event Publisher - User Online
//			kafkaTemplate.send("PresenceUpdated", presence);
//		}
//    }
//
//
//    public void disconnect(Long userId) {
//
//        //Mark as Offline
//        String userPresenceKey = generateUserPresenceKey(userId);
//        PresenceDTO presence = PresenceDTO
//                .builder()
//                .id(userId)
//                .status("OFFLINE")
//                .lastSeen(LocalDateTime.now())
//                .build();
//
//        redisTemplate.opsForValue().set(userPresenceKey, presence);
//
//        //Event Publisher - User Offline
//        kafkaTemplate.send("PresenceUpdated", presence);
//    }
//
//
//    public void removeOffline(Long userId){
//
//        String userPresenceKey = generateUserPresenceKey(userId);
//        PresenceDTO presence = redisTemplate.opsForValue().get(userPresenceKey);
//
//        //If still Offline...
//        if (presence.getStatus().equals("OFFLINE")) {
//
//            //Forgot it
//
//            //Remove it from Cache
//            redisTemplate.delete(userPresenceKey);
//
//            //Event Publisher - Presence Forgotten
//            kafkaTemplate.send("PresenceForgotten", userId);
//        }
//    }
//
//
//    public List<PresenceDTO> getPresences(List<Long> userIds){
//
//        //Fetch from Cache
//        Set<String> userPresenceKeys = userIds
//                .stream()
//                .map(this::generateUserPresenceKey)
//                .collect(Collectors.toSet());
//
//        return redisTemplate.opsForValue().multiGet(userPresenceKeys)
//					.stream()
//					.filter(Objects::nonNull)
//					.toList();
//    }
//
//
//
//
//    @KafkaListener(topics = "UserDeleted", groupId = "${spring.kafka.consumer.group-id}")
//    public void onUserDeleted(Long id){
//
//        //Forgot user presence
//        String userPresenceKey = generateUserPresenceKey(id);
//        redisTemplate.delete(userPresenceKey);
//    }
//
//
//
//
//
//    private String generateUserPresenceKey(Long id){
//        return String.format("%s", id);
//    }
//
//
//
//
//    public PresenceService(RedisTemplate<String, PresenceDTO> redisTemplate, KafkaTemplate<String, Object> kafkaTemplate) {
//        this.redisTemplate = redisTemplate;
//        this.kafkaTemplate = kafkaTemplate;
//    }
//
//    private final RedisTemplate<String, PresenceDTO> redisTemplate;
//    private final KafkaTemplate<String, Object> kafkaTemplate;
//}