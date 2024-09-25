package com.cesar.Conversation.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name="participants")
@Data
public class Participant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name="user_id")
    private Long userId;
    private String name;
    @Column(name="image_url")
    private String imageUrl;
    @Column(name="unread_messages")
    private Integer unreadMessages;

    @ManyToOne(targetEntity = Conversation.class, fetch = FetchType.EAGER)
    private Conversation conversation;

    public void incrementUnreadMessage(){
        unreadMessages ++;
    }
}