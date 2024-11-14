package com.cesar.Chat.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="conversations")
public class Conversation{

    @Id
    private UUID id;

    @OneToMany(mappedBy = "conversation", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Participant> participants = new ArrayList<>();

    @Column(name="recreate_for")
    private List<Long> recreateFor;

    @Column(name="created_at")
    private LocalDateTime createdAt;
    
    
    //Helper method for participants initialization
    public void addParticipants(List<Participant> participants) {
    	this.participants.addAll(participants);
    	participants.forEach(p -> p.setConversation(this));
    }
}