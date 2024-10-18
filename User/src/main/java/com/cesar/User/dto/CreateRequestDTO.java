package com.cesar.User.dto;

import lombok.Data;

@Data
public class CreateRequestDTO {
    private String username;
    private String email;
    private String password;
}