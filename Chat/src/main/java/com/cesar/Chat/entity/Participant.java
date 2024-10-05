package com.cesar.Chat.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

@Entity
@Table(name="participants")
@Data
@Builder
public class Participant {
    @Id
    @Column(unique = true)
    private Long id;
    private String name;
    @Column(name="image_url")
    private String profileImageUrl;
    @Column(name="unread_messages")
    private Integer unreadMessages;

    @ManyToOne(targetEntity = Conversation.class, fetch = FetchType.EAGER)
    private Conversation conversation;
}