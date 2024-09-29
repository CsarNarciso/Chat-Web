package com.cesar.Conversation.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class CreationResponseDTO {
    private Long id;
    private List<Long> participantsUsersIds;
    private boolean recreateForSomeone;
}