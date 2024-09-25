package com.cesar.Conversation.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PresenceStatusDTO {
    private Long userId;
    private String presenceStatus;
    private LocalDateTime lastSeen;
}