package com.cesar.MessageAPI.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name="messages")
@Data
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long conversationId;
    private Long senderId;
    private Long recipientId;
    private String content;
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime date;
}