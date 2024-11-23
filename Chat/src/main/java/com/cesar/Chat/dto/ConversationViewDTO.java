package com.cesar.Chat.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationViewDTO {
    private UUID id;
    private LocalDateTime createdAt;
    private ConversationRecipientDTO recipient;
    private LastMessageDTO lastMessage;
    private Long unreadMessagesCount;
    private boolean recreateForSomeone;
    private boolean participantDeleted; 
}