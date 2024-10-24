package com.cesar.Social.dto;

import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
public class ConversationCreatedDTO {
    private UUID id;
    private List<Long> createFor;
}