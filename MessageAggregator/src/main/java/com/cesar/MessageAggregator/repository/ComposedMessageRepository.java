package com.cesar.MessageAggregator.repository;

import com.cesar.MessageAggregator.entity.ComposedMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComposedMessageRepository extends JpaRepository<ComposedMessage, Long> {
    List<ComposedMessage> findByConversationId(Long conversationId);
}