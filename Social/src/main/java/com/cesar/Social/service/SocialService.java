package com.cesar.Social.service;

import com.cesar.Social.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class SocialService {


    @KafkaListener(topics = "PresenceUpdated", groupId = "${spring.kafka.consumer.group-id}")
    public void onPresenceUpdated(UserPresenceDTO presence){
        notifyInvolvedOnesOnUserNetwork(presence.getId(), presence);
    }

    @KafkaListener(topics = "PresenceForgotten", groupId = "${spring.kafka.consumer.group-id}")
    public void onPresenceForgotten(Long userId){
        notifyInvolvedOnesOnUserNetwork(userId, "PresenceForgotten");
    }

    @KafkaListener(topics = "UserUpdated", groupId = "${spring.kafka.consumer.group-id}")
    public void onUserUpdated(UserUpdatedDTO user){
        notifyInvolvedOnesOnUserNetwork(user.getId(), user);
    }

    @KafkaListener(topics = "UserDeleted", groupId = "${spring.kafka.consumer.group-id}")
    public void onUserDeleted(Long userId){
        notifyInvolvedOnesOnUserNetwork(userId, "UserDeleted");
    }



    private void notifyInvolvedOnesOnUserNetwork(Long userId, Object message){

        //Fetch user network (relationships)
        NetworkDTO network = relationshipService.getByUserId(userId);

        //For involved conversations
        network.getConversationIds()
            .forEach(id -> {
                websocketTemplate.convertAndSend(
                        String.format("/topic/conversation/%s", id),
                        message);
            });
    }



    public SocialService(SimpMessagingTemplate websocketTemplate, NetworkService relationshipService) {
        this.websocketTemplate = websocketTemplate;
        this.relationshipService = relationshipService;
    }

    private final SimpMessagingTemplate websocketTemplate;
    private final NetworkService relationshipService;
}