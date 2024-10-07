package com.cesar.Chat.dto;

import lombok.Data;
import java.util.List;

@Data
public class CreateConversationRqsDTO {
    private Long senderId;
    private List<Long> participantsIds;
}