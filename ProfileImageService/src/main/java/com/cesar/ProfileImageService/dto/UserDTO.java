package com.cesar.ProfileImageService.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDTO {
    private boolean hasImage;
    private String imageName;
    private String imageExtension;
    private String imagePath;
}
