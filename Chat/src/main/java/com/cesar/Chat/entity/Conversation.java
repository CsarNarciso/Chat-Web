package com.cesar.Chat.entity;

import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name="conversations")
@Data
@RequiredArgsConstructor
@Builder
public class Conversation implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name="created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;
    @Column(name="participants_ids")
    private List<Long> participantsIds;
    @Column(name="recreate_for")
    private List<Long> recreateFor;
}