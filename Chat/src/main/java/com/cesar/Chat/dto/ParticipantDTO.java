package com.cesar.Chat.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ParticipantDTO {
    private Long id;
    private Long userId;
    private String username;
    private String profileImageUrl;
    private String presenceStatus;
    private LocalDateTime lastSeen;
    private Integer unreadMessages;
}