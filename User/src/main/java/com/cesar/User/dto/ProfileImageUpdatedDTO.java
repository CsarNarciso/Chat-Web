package com.cesar.User.dto;

import lombok.Data;

@Data
public class ProfileImageUpdatedDTO {
    private Long userId;
    private String imageUrl;
}