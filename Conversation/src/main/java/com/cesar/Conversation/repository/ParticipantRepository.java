package com.cesar.Conversation.repository;

import com.cesar.Conversation.entity.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {
    @Modifying
    @Query("UPDATE Participant p " +
            "SET p.unreadMessages = p.unreadMessages + 1 " +
            "WHERE p.userId != :senderId AND" +
            "WHERE p.conversation.id = :conversationId")
    void increaseUnreadMessages(@Param("senderId") Long senderId, @Param("conversationId") Long conversationId);
}