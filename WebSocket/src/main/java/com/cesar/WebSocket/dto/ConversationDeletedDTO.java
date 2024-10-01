package com.cesar.WebSocket.dto;

import lombok.Data;

@Data
public class ConversationDeletedDTO {
    private Long conversationId;
    private Long participantId;
}