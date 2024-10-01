package com.cesar.User.dto;

import lombok.Data;

@Data
public class UpdateResponseDTO {
    private Long id;
    private String name;
    private String email;
}