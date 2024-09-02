package com.cesar.ChatServer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SendMessageDTO {

    private Long id;
    private Long senderId;
    private Long recipientId;
    private String content;
    private LocalDateTime date;
}