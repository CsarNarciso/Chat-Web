package com.cesar.Chat.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationDTO {
    private UUID id;
    private LocalDateTime createdAt;
    private ParticipantDTO recipient;
    private LastMessageDTO lastMessage;
    private Integer unreadMessagesCount;
}