package com.cesar.ConversationAPI.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

@Entity
@Table(name="conversations")
@Data
@Builder
public class Conversation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long[] participants;
    private String name;
    private int newMessagesAmount;
}