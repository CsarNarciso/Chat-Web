package com.cesar.Conversation.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ParticipantDTO {
    private Long id;
    private String name;
    private String imageUrl;
    private Integer unreadMessages;
    private String presenceStatus;
    private LocalDateTime lastSeen;
}