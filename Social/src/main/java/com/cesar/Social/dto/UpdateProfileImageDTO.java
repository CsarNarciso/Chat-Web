package com.cesar.Social.dto;

import lombok.Data;

@Data
public class UpdateProfileImageDTO {
    private Long userId;
    private String imageUrl;
}