package com.cesar.Chat.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ConversationRecipientDTO {
    private Long userId;
    private String username;
    private String profileImageUrl;
    private String presenceStatus;
    private LocalDateTime lastSeen;
    private boolean deleted;
}