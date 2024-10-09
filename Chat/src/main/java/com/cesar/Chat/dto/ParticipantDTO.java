package com.cesar.Chat.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class ParticipantDTO {
    private Long userId;
    private String username;
    private String profileImageUrl;
    private String presenceStatus;
    private LocalDateTime lastSeen;
}