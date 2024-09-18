package com.cesar.ConversationAPI.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDTO {
    private String name;
    private String profileImagePath;
}
