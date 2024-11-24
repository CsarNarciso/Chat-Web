package com.cesar.Chat.dto;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class UserDTO {
    private Long id;
    private String username;
    private String profileImageUrl;
    private boolean deleted;
}