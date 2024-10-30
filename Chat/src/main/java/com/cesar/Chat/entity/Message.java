package com.cesar.Chat.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name="messages")
public class Message implements Serializable {
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