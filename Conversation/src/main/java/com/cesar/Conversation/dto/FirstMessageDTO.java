package com.cesar.Conversation.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class FirstMessageDTO {
    private Long senderId;
    private Long recipientId;
    private String content;
    private LocalDateTime sentAt;
}