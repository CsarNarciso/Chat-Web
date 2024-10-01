package com.cesar.WebSocket.service;

import com.cesar.WebSocket.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketService {

    //Event Consumer - User Presence Status Change
    //when user connects/disconnects
    //Data: userId, status
    //Task: send notification to user conversations participants (either deleted or not)
    //(so, maybe we need to add another attribute to user entity -deletedConversations-, which are conversations that already
    // exists but user deleted it for itself)
    public void presenceStatusUpdated(PresenceStatusUpdatedDTO statusUpdated){
        statusUpdated.getuserConversationsParticipantsIds()
                .forEach(participantId -> {
                    template.convertAndSendToUser(
                            participantId.toString(),
                            "/queue/reply/",
                            statusUpdated);
                });
    }
    //Event Consumer - User details/profile Update
    //when User or ProfileImage Service updates a user details or profile image
    //Data: userId, updated data
    //Task: send notification to users who have a conversation with this user
    public void userDetailsUpdated(UserUpdatedDTO updatedUser){
        updatedUser.getuserConversationsParticipantsIds()
                .forEach(participantId -> {
                    template.convertAndSendToUser(
                            participantId.toString(),
                            "/queue/reply/",
                            updatedUser);
                });
    }
    //Event Consumer - User profile image Update
    //when ProfileImage Service updates user profile image
    //Data: userId, newImageUrl
    //Task: send notification to users who have a conversation with this user
    public void profileImageUpdated(ProfileImageUpdatedDTO updatedImage){
        updatedImage.getuserConversationsParticipantsIds()
                .forEach(participantId -> {
                    template.convertAndSendToUser(
                            participantId.toString(),
                            "/queue/reply/",
                            updatedImage);
                });
    }
    //Event Consumer - Conversation Created
    //when a first interaction message is sent and conversation services creates new conversation
    //Data: conversationDTO
    //Task: send new conversation data to each participant (ids)
    public void conversationCreated(ConversationCreatedDTO conversation){
        conversation.getParticipantsUsersIds()
                .forEach(participantId ->{
                    template.convertAndSendToUser(
                            participantId.toString(),
                            "/queue/reply",
                            conversation);
                });
    }
    //Event Consumer - Conversation Deleted
    //when a conversation services deletes conversation
    //Data: userId, conversationId
    //Task: send conversationId to user (so that it removes it from frontend)
    public void conversationDeleted(ConversationDeletedDTO conversation){
        template.convertAndSendToUser(
                conversation.getParticipantId().toString(),
                "/queue/reply/",
                conversation.getConversationId());
    }
    //Event Consumer - Message Sent
    //when a message is sent
    //Data: messageDTO
    //Task: push message to message conversation topic (by conversationId)
    public void messageSent(MessageSentDTO message){
        template.convertAndSend(
                "/topic/conversation/"+message.getConversationId(),
                message);
    }
    @Autowired
    private SimpMessagingTemplate template;
}