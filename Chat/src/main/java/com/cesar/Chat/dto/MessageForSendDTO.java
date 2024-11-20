package com.cesar.Chat.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class MessageForSendDTO {
    private UUID conversationId;
    private Long senderId;
    private String content;
    private boolean recreateForSomeone;
}