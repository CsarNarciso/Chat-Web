package com.cesar.Chat.repository;

import com.cesar.Chat.dto.UnreadMessagesDTO;
import com.cesar.Chat.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {

    @Query("SELECT m.conversation_id AS conversationId, COUNT(*) AS count FROM Message m " +
            "WHERE sender_id!=:senderId AND m.read=false AND m.conversation_id IN :conversationIds " +
            "GROUP BY m.conversation_id")
    List<UnreadMessagesDTO> getUnreadMessages(@Param("senderId") Long senderId, @Param("conversationIds") List<UUID> conversationIds);

    @Query("UPDATE m Message SET m.read=true WHERE m.sender_id!=:senderId AND conversation_id=:conversationId")
    void cleanConversationUnreadMessages(@Param("senderId") Long senderId, @Param("conversationId") UUID conversationId);

    List<Message> findAllByConversationId(UUID conversationId);

    @Query("SELECT m FROM Message m " +
            "WHERE m.conversation_id IN :conversationIds " +
            "GROUP BY m.id")
    List<Message> findAllByConversationIds(@Param("conversationIds") List<UUID> conversationIds);

    void deleteBySenderId(Long senderId);

    void deleteByConversationId(UUID conversationId);
}