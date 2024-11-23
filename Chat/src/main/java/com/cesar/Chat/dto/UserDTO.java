package com.cesar.Chat.dto;

import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String username;
    private String profileImageUrl;
    private boolean deleted;
}