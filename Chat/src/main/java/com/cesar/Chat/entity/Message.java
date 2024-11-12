package com.cesar.Chat.entity;

import java.time.LocalDateTime;
import java.util.UUID;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;

@Data
@Entity
@Table(name="messages")
public class Message {
    @Id
    private UUID id;
    @Column(name="conversation_id")
    private UUID conversationId;
    @Column(name = "sender_id")
    private Long senderId;
    private String content;
    @Column(name="sent_at")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime sentAt;
    private boolean read;
}