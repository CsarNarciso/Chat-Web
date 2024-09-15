package com.cesar.ProfileImageService.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UploadImageDTO {
    private byte[] imageBytes;
    private String actionPerformed;
}
