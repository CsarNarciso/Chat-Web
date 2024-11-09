package com.cesar.Chat.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name="conversations")
public class Conversation implements Serializable {

    @Id
    private UUID id;

    @OneToMany(targetEntity = Participant.class, fetch = FetchType.EAGER)
    @ToString.Exclude
    @JsonIgnore
    private List<Participant> participants;

    @Column(name="recreate_for")
    private List<Long> recreateFor;

    @Column(name="created_at")
    private LocalDateTime createdAt;
}