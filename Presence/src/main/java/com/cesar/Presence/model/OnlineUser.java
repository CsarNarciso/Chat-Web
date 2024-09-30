package com.cesar.Presence.model;

import lombok.Data;

@Data
public class OnlineUser {
    private Long id;
    private Long disconnectionHour;
    private String name;
    private byte[] profileImage;
    private String status;
}
