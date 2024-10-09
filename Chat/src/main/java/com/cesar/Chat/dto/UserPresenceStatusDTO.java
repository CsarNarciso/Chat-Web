package com.cesar.Chat.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserPresenceStatusDTO {
    private Long id;
    private String presenceStatus;
    private LocalDateTime lastSeen;
}