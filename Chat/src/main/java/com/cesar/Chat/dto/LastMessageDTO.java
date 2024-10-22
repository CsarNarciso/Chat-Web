package com.cesar.Chat.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class LastMessageDTO {
    private UUID id;
    private String content;
    private LocalDateTime sentAt;
}