package com.cesar.Chat.repository;

import com.cesar.Chat.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

public interface ConversationRepository extends JpaRepository<Conversation, UUID> {

    Conversation findByParticipantIds(List<Long> participantIds);

    @Query("SELECT c FROM Conversation c WHERE :participantId IN c.participant_ids GROUP BY c.id")
    List<Conversation> findByParticipantId(@Param("participantId") Long participantId);
}