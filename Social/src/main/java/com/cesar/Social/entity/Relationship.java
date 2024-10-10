package com.cesar.Social.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Table(name="relationships")
@Data
public class Relationship {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name="user_id")
    private Long userId;
    @Column(name="conversations_ids")
    private List<Long> conversationsIds;
}