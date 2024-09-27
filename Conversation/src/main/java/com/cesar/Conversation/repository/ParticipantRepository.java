package com.cesar.Conversation.repository;

import com.cesar.Conversation.entity.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {
    @Modifying
    @Query("UPDATE Participant participant " +
            "SET participant.unreadMessages=participant.unreadMessages+1 " +
            "WHERE participant.userId=:userId")
    void increaseUnreadMessages(Long userId);
    @Modifying
    @Query("UPDATE Participant participant SET participant.unreadMessages=0 WHERE participant.userId=:userId")
    void cleanUnreadMessages(Long userId);
}