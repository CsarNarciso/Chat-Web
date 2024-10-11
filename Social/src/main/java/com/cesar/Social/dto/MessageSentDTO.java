package com.cesar.Social.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MessageSentDTO {
    private Long id;
    private Long conversationId;
    private Long senderId;
    private String content;
    private LocalDateTime sentAt;
}