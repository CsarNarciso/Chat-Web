package com.cesar.Chat.entity;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "participants")
public class Participant{
    
	@Id
    @Column(name = "user_id")
    private Long userId;
    
    @ManyToOne
    @JoinColumn(name = "conversation_id")
    @ToString.Exclude
    @JsonIgnore
    private Conversation conversation;
    
    @OneToMany(mappedBy = "participant", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Message> messages = new ArrayList<Message>();
    
    public void addMessages(List<Message> messages) {
    	this.messages.addAll(messages);
    	messages.forEach(m -> m.setParticipant(this));
    }
}