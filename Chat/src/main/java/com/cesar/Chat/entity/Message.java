package com.cesar.Chat.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.redis.core.RedisHash;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name="messages")
@RedisHash("Message")
@Data
public class Message implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name="sender_id")
    private Long senderId;
    private String content;
    @Column(name="sent_at")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime sentAt;
    private boolean read;

    @ManyToOne
    @JoinColumn(name="conversation_id")
    private Conversation conversation;
}