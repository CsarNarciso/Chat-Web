package com.cesar.Social.dto;

import lombok.Data;

import java.util.List;

@Data
public class RelationshipDTO {
    private Long userId;
    private List<Long> conversationsIds;
}