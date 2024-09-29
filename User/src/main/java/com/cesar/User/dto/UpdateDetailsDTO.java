package com.cesar.User.dto;

import lombok.Data;

@Data
public class UpdateDetailsDTO {
    private Long id;
    private String username;
    private String email;
}