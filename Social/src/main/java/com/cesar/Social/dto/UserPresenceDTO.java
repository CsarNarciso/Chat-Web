package com.cesar.Social.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserPresenceDTO {
    private Long id;
    private String status;
    private LocalDateTime lastSeen;
}