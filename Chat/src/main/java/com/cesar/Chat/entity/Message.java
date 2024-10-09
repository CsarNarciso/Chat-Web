package com.cesar.Chat.entity;

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
    @Column(name="conversation_id")
    private Long conversationId;
    @Column(name="sender_id")
    private Long senderId;
    private String content;
    @Column(name="sent_at")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime sentAt;
    private boolean read;
}