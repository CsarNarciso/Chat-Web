package com.cesar.MessageAPI.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MessageDTO {
    private Long id;
    private Long conversationId;
    private Long senderId;
    private Long recipientId;
    private String content;
    private LocalDateTime sentAt;
}