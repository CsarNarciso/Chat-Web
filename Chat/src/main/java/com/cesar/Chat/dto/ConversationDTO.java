package com.cesar.Chat.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ConversationDTO {
    private Long id;
    private LocalDateTime createdAt;
    private String lastMessageContent;
    private LocalDateTime lastMessageSentAt;
    private List<ParticipantDTO> participants;
}