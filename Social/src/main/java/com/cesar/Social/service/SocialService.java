package com.cesar.Social.service;

import com.cesar.Social.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class SocialService {

    //Event Consumer - User Presence Status Change
    //when user connects/disconnects on Presence Service
    //Data: userId, status
    //Task: send notification to users who have a conversation with this user
    public void presenceStatusUpdated(PresenceStatusDTO statusUpdated){
        RelationshipDTO relationship = relationshipService.getByUserId(statusUpdated.getUserId());
        relationship.getConversationsIds()
                .forEach(participantId -> {
                    template.convertAndSendToUser(
                            participantId.toString(),
                            "/queue/reply/",
                            statusUpdated);
                });
    }
    //Event Consumer - User details/profile Update
    //when User Service updates a user details
    //Data: userId, updated data
    //Task: send notification to users who have a conversation with this user
    public void userDetailsUpdated(UpdateUserDTO updatedUser){
        RelationshipDTO relationship = relationshipService.getByUserId(updatedUser.getId());
        relationship.getConversationsIds()
                .forEach(participantId -> {
                    template.convertAndSendToUser(
                            participantId.toString(),
                            "/queue/reply/",
                            updatedUser);
                });
    }
    //Event Consumer - User profile image Update
    //when User Service updates a user profile image
    //Data: userId, newImageUrl
    //Task: send notification to users who have a conversation with this user
    public void profileImageUpdated(UpdateProfileImageDTO updatedImage){
        RelationshipDTO relationship = relationshipService.getByUserId(updatedImage.getUserId());
        relationship.getConversationsIds()
                .forEach(participantId -> {
                    template.convertAndSendToUser(
                            participantId.toString(),
                            "/queue/reply/",
                            updatedImage);
                });
    }

    //----Event Consumer - User Deleted---
    //when User Service deletes a user
    //Data: userId
    //Task: notify deleted user relationships

    @Autowired
    private RelationshipService relationshipService;
    @Autowired
    private SimpMessagingTemplate template;
}