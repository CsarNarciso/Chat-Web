package com.cesar.Chat.dto;

import lombok.Data;

@Data
public class UnreadMessagesDTO {
    private Long conversationId;
    private Integer count;
}