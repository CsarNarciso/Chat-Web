package com.cesar.Chat.dto;

import lombok.Data;

@Data
public class MessageForSendDTO {
    private Long conversationId;
    private Long senderId;
    private String content;
}