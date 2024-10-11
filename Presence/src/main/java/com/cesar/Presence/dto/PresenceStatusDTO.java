package com.cesar.Presence.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class PresenceStatusDTO {
    private Long userId;
    private String status;
    private LocalDateTime lastSeen;
}