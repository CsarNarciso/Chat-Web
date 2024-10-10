package com.cesar.Social.dto;

import lombok.Data;
import java.util.List;

@Data
public class ConversationCreatedDTO {
    private Long conversationId;
    private String action;
    private List<Long> participantsIds;
    private List<Long> recreatedForIds;
}