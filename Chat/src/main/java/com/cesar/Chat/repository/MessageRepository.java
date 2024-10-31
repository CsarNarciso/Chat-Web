package com.cesar.Chat.repository;

import com.cesar.Chat.dto.UnreadMessagesDTO;
import com.cesar.Chat.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {

    @Query("SELECT m.conversationId AS conversationId, COUNT(m) AS count FROM Message m " +
            "WHERE m.senderId!=:senderId AND m.read=false AND m.conversationId IN :conversationIds " +
            "GROUP BY m.conversationId")
    List<UnreadMessagesDTO> getUnreadMessages(@Param("senderId") Long senderId,
                                              @Param("conversationIds") List<UUID> conversationIds);

    @Modifying
    @Query("UPDATE Message m SET m.read=true " +
            "WHERE m.senderId!=:senderId AND m.conversationId=:conversationId")
    void cleanConversationUnreadMessages(@Param("senderId") Long senderId,
                                         @Param("conversationId") UUID conversationId);

    @Query("SELECT m FROM Message m WHERE m.conversationId IN :conversationIds")
    List<Message> findAllByConversationIds(@Param("conversationIds") List<UUID> conversationIds);

    List<Message> findAllByConversationId(UUID conversationId);

    void deleteBySenderId(Long senderId);

    void deleteByConversationId(UUID conversationId);
}