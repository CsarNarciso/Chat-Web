package com.cesar.Chat.repository;

import com.cesar.Chat.entity.Conversation;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.UUID;

public interface ConversationRepository extends JpaRepository<Conversation, UUID> {

    Conversation findByParticipantIds(List<Long> participantIds);

    @Query("SELECT c FROM conversations c " +
            "WHERE :participantId IN c.participant_ids" +
            "GROUP BY c.id")
    List<Conversation> findByParticipantId(@Param("participantId") Long participantId);
}