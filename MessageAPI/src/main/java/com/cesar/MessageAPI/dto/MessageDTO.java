package com.cesar.MessageAPI.dto;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO {

    private Long id;
    private Long senderId;
    private Long recipientId;
    private String content;
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime date;
}
