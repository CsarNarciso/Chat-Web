package com.cesar.Chat.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Data
@AllArgsConstructor
@Builder
@Table(name="conversations")
public class Conversation implements Serializable {
    @Id()
    private UUID id;
    @Column(name = "participant_ids")
    private List<Long> participantIds;
    @Column(name="recreate_for")
    private List<Long> recreateFor;
    @Column(name="created_at")
    private LocalDateTime createdAt;
}