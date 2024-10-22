package com.cesar.Chat.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class ConversationCreatedDTO {
    private UUID id;
    private List<Long> createFor;
}