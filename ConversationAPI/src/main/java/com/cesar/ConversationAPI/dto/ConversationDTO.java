package com.cesar.ConversationAPI.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ConversationDTO {
    private String name;
    private int newMessagesAmount;
    private List<ParticipantDTO> participants;
}