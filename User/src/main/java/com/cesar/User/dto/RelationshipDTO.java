package com.cesar.User.dto;

import lombok.Data;
import java.util.List;

@Data
public class RelationshipDTO {
    private List<Long> conversationsIds;
}