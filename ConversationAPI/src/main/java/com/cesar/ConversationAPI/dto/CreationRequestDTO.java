package com.cesar.ConversationAPI.dto;

import lombok.Data;

import java.util.List;

@Data
public class CreationRequestDTO {
    private Long senderId;
    private List<Long> participantsIds;
    private boolean group;
    private String name;
}