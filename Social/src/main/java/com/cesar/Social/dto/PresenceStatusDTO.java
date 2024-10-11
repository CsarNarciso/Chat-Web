package com.cesar.Social.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PresenceStatusDTO {
    private Long userId;
    private String status;
    private LocalDateTime lastSeen;
}