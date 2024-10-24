package com.cesar.Social.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name="networks")
@Data
public class Network implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name="user_id")
    private Long userId;
    @Column(name="conversation_ids")
    private List<UUID> conversationIds;
}