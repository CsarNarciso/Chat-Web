package com.cesar.Chat.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Builder
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
}