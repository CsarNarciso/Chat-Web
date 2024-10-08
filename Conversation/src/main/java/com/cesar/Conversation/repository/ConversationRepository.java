package com.cesar.Conversation.repository;

import com.cesar.Conversation.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    Conversation findByParticipantsIds(List<Long> participantsIds);
}