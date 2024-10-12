package com.cesar.Chat.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ConversationDeletedDTO {
    private Long participantId;
    private Long conversationId;
}