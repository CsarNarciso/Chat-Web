package com.cesar.Chat.dto;

import java.util.UUID;
import lombok.Data;

@Data
public class UnreadMessagesDTO {
    private UUID conversationId;
    private Long count;
}