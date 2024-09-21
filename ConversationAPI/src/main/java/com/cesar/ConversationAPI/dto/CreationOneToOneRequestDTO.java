package com.cesar.ConversationAPI.dto;

import lombok.Data;

import java.util.List;

@Data
public class CreationOneToOneRequestDTO {
    private List<Long> participantsIds;
}