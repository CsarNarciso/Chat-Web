package com.cesar.Chat.repository;

import com.cesar.Chat.dto.UnreadMessagesDTO;
import com.cesar.Chat.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByConversationId(Long conversationId);

    @Query("SELECT c.id AS conversationId, COUNT(m) AS count " +
            "FROM Messages m JOIN m.conversation c" +
            "WHERE m.senderId!=:senderId AND m.read=false" +
            "GROUP BY c.id")
    List<UnreadMessagesDTO> getUnreadMessages(@Param("senderId") Long senderId);

    @Query("UPDATE m Messages m SET m.read=true WHERE m.conversationId=:conversationId AND m.senderId!=:senderId")
    void cleanConversationUnreadMessages(Long conversationId, Long senderId);
}