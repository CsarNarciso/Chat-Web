package com.cesar.Conversation.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeletionResponseDTO {
    private Long conversationId;
    private Long participantId;
    private boolean recreateForSomeone;
    private boolean permanently;
}