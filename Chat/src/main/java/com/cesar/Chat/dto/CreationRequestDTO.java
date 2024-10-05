package com.cesar.Chat.dto;

import lombok.Data;
import java.util.List;

@Data
public class CreationRequestDTO {
    private Long senderId;
    private List<Long> participantsIds;
}