package com.cesar.Conversation.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ConversationDTO {
    private Long id;
    private LocalDateTime createdAt;
    private List<ParticipantDTO> participants;
}