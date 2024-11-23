package com.cesar.Chat.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConversationDTO implements Serializable {
    private UUID id;
    private List<Long> participants;
    private List<Long> recreateFor;
    private LocalDateTime createdAt;
    private boolean participantDeleted;
}