package com.cesar.Chat.repository;

import com.cesar.Chat.dto.UnreadMessagesDTO;
import com.cesar.Chat.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("SELECT m.conversationId AS conversationId, COUNT(m) AS count " +
            "FROM Message m" +
            "WHERE m.senderId!=:senderId AND m.read=false")
    List<UnreadMessagesDTO> getUnreadMessages(@Param("senderId") Long senderId);

    @Modifying
    @Query("UPDATE m Message m SET m.read=true " +
            "WHERE m.conversationId=:conversationId AND m.senderId!=:senderId")
    void cleanConversationUnreadMessages(Long conversationId, Long senderId);

    List<Message> findByConversationId(Long conversationId);
    void deleteBySenderId(Long senderId);
    void deleteByConversationId(Long conversationId);
}