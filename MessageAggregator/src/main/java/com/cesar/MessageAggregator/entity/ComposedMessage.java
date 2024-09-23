package com.cesar.MessageAggregator.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name="composedMessages")
@Data
public class ComposedMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long conversationId;
    private Long senderId;
    private String senderName;
    private String senderProfileImageUrl;
    private Long recipientId;
    private String recipientName;
    private String recipientProfileImageUrl;
    private String content;
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime sentAt;
}