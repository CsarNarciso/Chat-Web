package com.cesar.Social.dto;

import lombok.Data;

@Data
public class UpdateUserDTO {
    private Long id;
    private String username;
    private String email;
}