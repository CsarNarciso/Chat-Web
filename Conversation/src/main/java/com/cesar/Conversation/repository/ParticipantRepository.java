package com.cesar.Conversation.repository;

import com.cesar.Conversation.entity.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {

    List<Long> findIdByUserId(List<Long> usersIds);

    @Modifying
    @Query("UPDATE Participant participant " +
            "SET participant.unreadMessages=participant.unreadMessages+1 " +
            "WHERE NOT participant.userId=:userId AND" +
            "WHERE participant.conversation.id=:conversationId")
    void increaseUnreadMessages(@Param("userId") Long userId, @Param("conversationId") Long conversationId);
    @Modifying
    @Query("UPDATE Participant participant SET participant.unreadMessages=0 WHERE participant.userId=:userId")
    void cleanUnreadMessages(@Param("userId") Long userId);
}