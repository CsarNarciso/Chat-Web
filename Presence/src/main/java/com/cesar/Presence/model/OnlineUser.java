package com.cesar.Presence.model;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class OnlineUser {
    private Long userId;
    private String status;
    private LocalDateTime lastSeen;
}