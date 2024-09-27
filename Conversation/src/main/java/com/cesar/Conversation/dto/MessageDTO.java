package com.cesar.Conversation.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MessageDTO {
    private Long recipientId;
    private Long conversationId;
    private String content;
    private LocalDateTime sentAt;
}