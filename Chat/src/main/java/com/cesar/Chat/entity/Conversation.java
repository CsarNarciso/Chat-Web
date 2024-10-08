package com.cesar.Chat.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name="conversations")
@Data
@Builder
public class Conversation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name="created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;
    @Column(name="recreate_for")
    private List<Long> recreateFor;

    @OneToMany(targetEntity = Participant.class, fetch = FetchType.EAGER)
    private List<Participant> participants;
    @Column(name="participants_ids")
    private List<Long> participantsIds;
}