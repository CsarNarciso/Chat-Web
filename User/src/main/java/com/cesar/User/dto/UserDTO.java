package com.cesar.User.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class UserDTO {
    private Long id;
    private String username;
    private String profileImageUrl;
    private List<Long> conversationsIds;
}