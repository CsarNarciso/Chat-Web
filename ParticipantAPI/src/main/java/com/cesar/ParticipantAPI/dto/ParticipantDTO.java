package com.cesar.ParticipantAPI.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ParticipantDTO {
    private Long id;
    private String name;
    private String presenceStatus;
    private byte[] profileImage;
    private boolean sender;
}
