package com.cesar.User.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateResponseDTO {
    private UserDTO userResponse;
    private UploadImageResponseDTO profileImageUploadResponse;
}