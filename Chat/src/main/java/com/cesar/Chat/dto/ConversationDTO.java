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
    private ParticipantDTO recipient;
    private List<Long> participantsIds;
    private Integer unreadMessages;
}