package com.cesar.Chat.dto;

import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ConversationCreatedDTO {
    private UUID id;
    private List<Long> createFor;
}