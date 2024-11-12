package com.cesar.Chat.dto;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Data;

@Data
public class LastMessageDTO {
    private UUID id;
    private String content;
    private LocalDateTime sentAt;
}