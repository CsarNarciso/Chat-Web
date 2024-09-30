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
    private Long id;
    private String name;
    @Column(name="image_url")
    private String imageUrl;
    @Column(name="unread_messages")
    private Integer unreadMessages;

    @ManyToOne(targetEntity = Conversation.class, fetch = FetchType.EAGER)
    private Conversation conversation;
}