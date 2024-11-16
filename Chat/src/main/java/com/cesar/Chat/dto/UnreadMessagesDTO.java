package com.cesar.Chat.dto;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UnreadMessagesDTO {
    private UUID conversationId;
    private Long count;
}