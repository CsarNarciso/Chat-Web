package com.cesar.Conversation.repository;

import com.cesar.Conversation.entity.Participant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {
    List<Participant> findAllByConversationId(List<Long> conversationsIds);
}