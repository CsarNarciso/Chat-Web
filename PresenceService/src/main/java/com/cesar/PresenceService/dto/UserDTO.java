package com.cesar.PresenceService.dto;

import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String name;
    private byte[] profileImage;
}
