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

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="conversations")
public class Conversation{

    @Id
    private UUID id;

    @Column(name="recreate_for")
    private List<Long> recreateFor;

    @Column(name="created_at")
    private LocalDateTime createdAt;
    
    @OneToMany(mappedBy = "conversation", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Participant> participants = new ArrayList<>();
    
    @OneToMany(mappedBy = "conversation", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Message> messages = new ArrayList<Message>();
    
    
    public void addParticipants(List<Participant> participants) {
    	this.participants.addAll(participants);
    	participants.forEach(p -> p.setConversation(this));
    }
    
    public void addMessages(List<Message> messages) {
    	this.messages.addAll(messages);
    	messages.forEach(m -> m.setConversation(this));
    }
}