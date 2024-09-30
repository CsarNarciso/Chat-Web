package com.cesar.Presence.dto;

import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String name;
    private byte[] profileImage;
}
