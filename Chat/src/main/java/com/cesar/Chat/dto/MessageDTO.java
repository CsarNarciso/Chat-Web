package com.cesar.Chat.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageDTO implements Serializable {
    private UUID id;
    private UUID conversationId;
    private Long senderId;
    private String content;
    private LocalDateTime sentAt;
	private boolean read;
}