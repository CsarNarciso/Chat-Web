package com.cesar.Chat.dto;

import lombok.Data;

@Data
public class MessageForInitDTO {
    private Long senderId;
    private Long recipientId;
    private String content;
}