package com.cesar.ProfileImage.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateResponseDTO {
    private Long userId;
    private String imageUrl;
}