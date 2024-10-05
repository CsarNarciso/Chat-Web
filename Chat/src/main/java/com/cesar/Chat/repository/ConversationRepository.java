package com.cesar.Chat.repository;

import com.cesar.Chat.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {}