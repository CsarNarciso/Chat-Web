package com.cesar.Chat.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeleteConversationRspDTO {
    private Long conversationId;
    private Long participantId;
    private boolean recreateForSomeone;
    private boolean permanently;
}