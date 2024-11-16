package com.cesar.Chat.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.cesar.Chat.dto.UnreadMessagesDTO;
import com.cesar.Chat.entity.Message;
import jakarta.transaction.Transactional;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {

    @Query("SELECT new com.cesar.Chat.dto.UnreadMessagesDTO(m.conversation.id, COUNT(m)) FROM Message m " +
            "WHERE m.senderId!=:senderId AND m.read=false AND m.conversation.id IN :conversationIds " +
            "GROUP BY m.conversation.id")
    List<UnreadMessagesDTO> getUnreadMessages(@Param("senderId") Long senderId,
                                              @Param("conversationIds") List<UUID> conversationIds);
    @Modifying
    @Transactional
    @Query("UPDATE Message m SET m.read=true " +
            "WHERE m.senderId!=:senderId AND m.conversation.id=:conversationId")
    void cleanConversationUnreadMessages(@Param("senderId") Long senderId,
                                         @Param("conversationId") UUID conversationId);

    @Query("SELECT m FROM Message m WHERE m.conversation.id IN :conversationIds")
    List<Message> findAllByConversationIds(@Param("conversationIds") List<UUID> conversationIds);

    @Query("SELECT m FROM Message m WHERE m.conversation.id = :conversationId")
    List<Message> findAllByConversationId(@Param("conversationId") UUID conversationId);

    void deleteBySenderId(Long senderId);
}