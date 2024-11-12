package com.cesar.Chat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageDTO {
    private UUID id;
    private UUID conversationId;
    private Long senderId;
    private String content;
    private LocalDateTime sentAt;
	private boolean read;
}