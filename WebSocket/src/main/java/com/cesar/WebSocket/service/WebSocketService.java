package com.cesar.WebSocket.service;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Service;

@Service
public class WebSocketService {

    //Event Consumer - User Presence Status Change
    //when user connects/disconnects
    //Data: userId, status
    //Task: send notification to user conversations participants (either deleted or not)

    //Event Consumer - Conversation Created
    //when a first interaction message is sent and conversation services creates new conversation
    //Data: conversationDTO
    //Task: send new conversation data to each participant (ids)

    //Event Consumer - Message Sent
    //when a message is sent
    //Data: messageDTO
    //Task: push message to message conversation topic (by conversationId)

}