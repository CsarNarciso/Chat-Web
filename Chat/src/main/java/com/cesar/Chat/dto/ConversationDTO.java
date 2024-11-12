package com.cesar.Chat.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ConversationDTO implements Serializable {
    private UUID id;
    private List<Long> recreateFor;
    private LocalDateTime createdAt;
}