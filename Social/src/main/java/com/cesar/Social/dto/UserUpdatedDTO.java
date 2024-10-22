package com.cesar.Social.dto;

import lombok.Data;

@Data
public class UserUpdatedDTO {
    private Long id;
    private String username;
    private String profileImageUrl;
}