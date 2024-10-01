package com.cesar.WebSocket.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PresenceStatusUpdatedDTO {
    private Long userId;
    private String status;
    private LocalDateTime lastSeen;
}