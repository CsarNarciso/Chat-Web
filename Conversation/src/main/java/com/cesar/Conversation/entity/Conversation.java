package com.cesar.Conversation.entity;

import com.cesar.Conversation.model.Participant;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name="conversations")
@Data
public class Conversation {
    private Long id;
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;
    private List<Participant> participantsDetails;
}