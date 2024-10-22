package com.cesar.Chat.repository;

import com.cesar.Chat.dto.UnreadMessagesDTO;
import com.cesar.Chat.entity.Message;
import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface MessageRepository extends CassandraRepository<Message, UUID> {

    @Query("SELECT conversation_id, COUNT(*) FROM messages " +
            "WHERE sender_id!=?0 AND read=false AND conversation_id IN ?1 " +
            "GROUP BY conversation_id")
    List<UnreadMessagesDTO> getUnreadMessages(Long senderId, List<UUID> conversationsIds);

    @Query("UPDATE messages SET read=true WHERE sender_id!=?0 AND conversation_id=?1")
    void cleanConversationUnreadMessages(Long senderId, UUID conversationId);

    @AllowFiltering
    List<Message> findAllByConversationId(UUID conversationId);

    @AllowFiltering
    List<Message> findAllByConversationIds(List<UUID> conversationIds);

    @AllowFiltering
    void deleteBySenderId(Long senderId);

    @AllowFiltering
    void deleteByConversationId(UUID conversationId);
}