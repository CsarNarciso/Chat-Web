package com.cesar.Chat.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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

    @Column(name="created_at")
    private LocalDateTime createdAt;
    
    @Column(name="recreate_for")
    private List<Long> recreateFor;
    
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name="conversations_participants", joinColumns = @JoinColumn(name="conversation_id"))
    @Column(name="user_id")
    private List<Long> participants = new ArrayList<>();
    
    @OneToMany(mappedBy = "conversation", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Message> messages = new ArrayList<>();
    
    public void addMessage(Message message) {
    	this.messages.add(message);
    	message.setConversation(this);
    }
    
    public void addMessages(List<Message> messages) {
    	this.messages.addAll(messages);
    	messages.forEach(m -> m.setConversation(this));
    }
}