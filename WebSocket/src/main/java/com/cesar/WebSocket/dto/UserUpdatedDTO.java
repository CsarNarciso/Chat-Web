package com.cesar.WebSocket.dto;

import lombok.Data;

@Data
public class UserUpdatedDTO {
    private Long id;
    private String username;
    private String email;
}