package com.cesar.Chat.repository;

import com.cesar.Chat.entity.Conversation;
import com.cesar.Chat.entity.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, UUID> {

    Conversation findByParticipants(List<Participant> participants);

    @Query("SELECT c FROM Conversation c " +
            "JOIN c.participants p " +
            "WHERE p.id = :participantId " +
            "GROUP BY c.id")
    List<Conversation> findByParticipantId(@Param("participantId") Long participantId);
}