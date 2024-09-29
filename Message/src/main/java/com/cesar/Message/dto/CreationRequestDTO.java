package com.cesar.Message.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CreationRequestDTO {
    private Long id;
    private Long conversationId;
    private Long senderId;
    private Long recipientId;
    private String content;
    private LocalDateTime sentAt;
}