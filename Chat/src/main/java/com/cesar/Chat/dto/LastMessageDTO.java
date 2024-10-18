package com.cesar.Chat.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class LastMessageDTO {
    private Long id;
    private String content;
    private LocalDateTime sentAt;
}