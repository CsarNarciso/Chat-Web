package com.cesar.WebSocket.dto;

import lombok.Data;

@Data
public class ProfileImageUpdatedDTO {
    private Long userId;
    private String imageUrl;
}