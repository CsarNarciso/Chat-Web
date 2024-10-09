package com.cesar.Chat.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserPresenceDTO {
    private Long id;
    private String presenceStatus;
    private LocalDateTime lastSeen;
}