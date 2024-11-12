package com.cesar.Chat.dto;

import java.io.Serializable;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ParticipantDTO implements Serializable {
    private Long userId;
}