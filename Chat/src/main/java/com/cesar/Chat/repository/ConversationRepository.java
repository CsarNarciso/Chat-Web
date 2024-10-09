package com.cesar.Chat.repository;

import com.cesar.Chat.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    Conversation findByParticipantsIds(List<Long> participantsIds);
    @Query("SELECT c FROM Conversation c WHERE :participantId IN c.participantsIds")
    List<Conversation> findByParticipantId(Long participantId);
}