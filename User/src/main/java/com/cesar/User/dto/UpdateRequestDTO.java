package com.cesar.User.dto;

import lombok.Data;

@Data
public class UpdateRequestDTO {
    private Long id;
    private String username;
    private String email;
}