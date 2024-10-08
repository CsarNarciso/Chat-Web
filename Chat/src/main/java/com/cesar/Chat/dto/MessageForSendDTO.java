package com.cesar.Chat.dto;

import lombok.Data;

@Data
public class MessageForSendDTO {
    private Long senderId;
    private Long recipientId;
    private Long conversationId;
    private String content;
}