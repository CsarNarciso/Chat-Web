package com.cesar.Conversation.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

@Entity
@Table(name="participants")
@Data
@Builder
public class Participant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long conversationId;
    private Long userId;
    private String name;
    private String imageUrl;
    private Integer unreadMessages;
}