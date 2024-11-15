package com.cesar.Chat.entity;

import java.time.LocalDateTime;
import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;
import lombok.ToString;

@Data
@Entity
@Table(name="messages")
public class Message {
    @Id
    private UUID id;
    private String content;
    @Column(name="sent_at")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime sentAt;
    private boolean read;
    
    @ManyToOne
    @JoinColumn(name = "conversation_id")
    @ToString.Exclude
    @JsonIgnore
    private Conversation conversation;
}