package com.cesar.Conversation.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ParticipantDTO {
    private Long userId;
    private Long conversationId;
    private String name;
    private String imageUrl;
    private Integer unreadMessages;
    private String presenceStatus;
    private LocalDateTime lastSeen;
}