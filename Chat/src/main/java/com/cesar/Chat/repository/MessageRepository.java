package com.cesar.Chat.repository;

import com.cesar.Chat.dto.UnreadMessagesDTO;
import com.cesar.Chat.entity.Message;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {

    @Query("SELECT m.conversation_id AS conversationId, COUNT(m) AS count FROM messages m " +
            "WHERE m.sender_id!=:senderId AND m.read=false AND m.conversation_id IN :conversationIds " +
            "GROUP BY m.conversation_id")
    List<UnreadMessagesDTO> getUnreadMessages(@Param("senderId") Long senderId,
                                              @Param("conversationIds") List<UUID> conversationIds);

    @Query("UPDATE messages m SET m.read=true " +
            "WHERE m.sender_id!=:senderId AND conversation_id=:conversationId")
    void cleanConversationUnreadMessages(@Param("senderId") Long senderId,
                                         @Param("conversationId") UUID conversationId);

    List<Message> findAllByConversationId(UUID conversationId);

    @Query("SELECT m FROM messages m WHERE m.conversation_id IN :conversationIds")
    List<Message> findAllByConversationIds(@Param("conversationIds") List<UUID> conversationIds);

    void deleteBySenderId(Long senderId);

    void deleteByConversationId(UUID conversationId);
}