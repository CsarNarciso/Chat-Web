package com.cesar.Conversation.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class CreateConversationRqsDTO {
    private Long senderId;
    private List<Long> participantsIds;
}