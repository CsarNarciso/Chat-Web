package com.cesar.Chat.entity;

import java.io.Serializable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "participants")
public class Participant implements Serializable {
    
	@Id
    @Column(name = "user_id")
    private Long userId;
    
    @ManyToOne(targetEntity = Conversation.class)
    private Conversation conversation;
}