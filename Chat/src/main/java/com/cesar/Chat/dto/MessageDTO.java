package com.cesar.Chat.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class MessageDTO {
    private UUID id;
    private UUID conversationId;
    private Long senderId;
    private String content;
    private LocalDateTime sentAt;
}