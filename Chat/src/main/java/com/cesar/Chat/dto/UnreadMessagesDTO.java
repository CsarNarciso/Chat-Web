package com.cesar.Chat.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class UnreadMessagesDTO {
    private UUID conversationId;
    private Integer count;
}