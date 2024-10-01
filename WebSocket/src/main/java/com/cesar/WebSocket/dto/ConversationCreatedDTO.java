package com.cesar.WebSocket.dto;

import lombok.Data;
import java.util.List;

@Data
public class ConversationCreatedDTO {
    private Long id;
    private List<Long> participantsUsersIds;
}