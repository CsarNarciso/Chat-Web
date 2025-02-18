package com.cesar.User.dto;

import java.util.List;
import java.util.UUID;

import lombok.Data;

@Data
public class NetworkDTO {
    private Long userId;
    private List<UUID> conversationIds;
}