package com.cesar.Conversation.dto;

import com.cesar.Conversation.model.Participant;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ConversationDTO {
    private Long id;
    private LocalDateTime createdAt;
    private List<Participant> participantsDetails;
}