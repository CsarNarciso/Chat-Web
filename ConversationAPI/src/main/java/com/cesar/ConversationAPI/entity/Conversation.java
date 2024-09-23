package com.cesar.ConversationAPI.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name="conversations")
@Data
public class Conversation {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    private List<Long> participantsIds;
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;
}