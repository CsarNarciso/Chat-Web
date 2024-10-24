package com.cesar.Social.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class ConversationDeletedDTO {
    private UUID id;
    private Long participantId;
    private boolean permanently;
}