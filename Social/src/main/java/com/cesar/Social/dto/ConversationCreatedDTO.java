package com.cesar.Social.dto;

import lombok.Data;
import java.util.List;

@Data
public class ConversationCreatedDTO {
    private Long id;
    private List<Long> createFor;
}