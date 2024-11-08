package com.cesar.Chat.entity;

import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name="conversations")
public class Conversation implements Serializable {

    @Id
    private UUID id;

    @ManyToMany
    @JoinTable(
            name = "conversation_participants",
            joinColumns = @JoinColumn(name = "conversation_id"),
            inverseJoinColumns = @JoinColumn(name = "participant_id")
    )
    private List<Participant> participants;

    @Column(name="recreate_for")
    private List<Long> recreateFor;

    @Column(name="created_at")
    private LocalDateTime createdAt;
}