package com.cesar.Conversation.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class DeletionResponseDTO {
    private Long conversationId;
    private Long participantId;
    private List<Long> recreateFor;
    private boolean permanently;
}