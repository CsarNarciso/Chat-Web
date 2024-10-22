package com.cesar.Chat.dto;

import lombok.Builder;
import lombok.Data;
import java.util.UUID;

@Data
@Builder
public class ConversationDeletedDTO {
    private UUID id;
    private Long participantId;
    private boolean permanently;
}