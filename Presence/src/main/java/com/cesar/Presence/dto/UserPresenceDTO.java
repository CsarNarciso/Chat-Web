package com.cesar.Presence.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class UserPresenceDTO {
    private Long id;
    private String status;
    private LocalDateTime lastSeen;
}