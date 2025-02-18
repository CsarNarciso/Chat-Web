package com.cesar.User.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PresenceDTO {
    private Long id;
    private String status;
    private LocalDateTime lastSeen;
}