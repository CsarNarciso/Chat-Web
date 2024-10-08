package com.cesar.Message.dto;

import lombok.Data;

@Data
public class SendMessageRqsDTO {
    private Long senderId;
    private Long recipientId;
    private Long conversationId;
    private String content;
}