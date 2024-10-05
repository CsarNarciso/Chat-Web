package com.cesar.Chat.repository;

import com.cesar.Chat.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByConversationId(Long conversationId);
}