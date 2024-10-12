package com.cesar.Chat.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class ConversationCreatedDTO {
    private Long conversationId;
    private List<Long> createFor;
}