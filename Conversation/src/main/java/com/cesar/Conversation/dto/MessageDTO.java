package com.cesar.Conversation.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MessageDTO {
    private Long id;
    private Long conversationId;
    private Long senderId;
    private String content;
    private LocalDateTime sentAt;
}