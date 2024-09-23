package com.cesar.Message.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MessageDTO {
    private Long id;
    private Long conversationId;
    private Long senderId;
    private String senderName;
    private String senderProfileImageUrl;
    private Long recipientId;
    private String recipientName;
    private String recipientProfileImageUrl;
    private String content;
    private LocalDateTime sentAt;
}