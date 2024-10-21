package com.cesar.Social.dto;

import lombok.Data;
import java.util.List;

@Data
public class NetworkDTO {
    private Long userId;
    private List<Long> conversationIds;
}