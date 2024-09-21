package com.cesar.ConversationAPI.repository;

import com.cesar.ConversationAPI.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    List<Conversation> findByParticipantId(Long participantId);
}