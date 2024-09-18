package com.cesar.ConversationAPI.dto;

import lombok.*;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreationResponseDTO {
    private Map<Long, ConversationDTO> participantsConversationData;
}