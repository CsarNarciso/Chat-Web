package com.cesar.Social.dto;

import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
public class NetworkDTO {
    private Long userId;
    private List<UUID> conversationIds;
}