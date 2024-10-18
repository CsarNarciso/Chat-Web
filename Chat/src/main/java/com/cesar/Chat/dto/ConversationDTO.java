package com.cesar.Chat.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class ConversationDTO {
    private Long id;
    private LocalDateTime createdAt;
    private ParticipantDTO recipient;
    private LastMessageDTO lastMessage;
    private Integer unreadMessagesCount;
}