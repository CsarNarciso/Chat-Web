package com.cesar.Chat.repository;

import com.cesar.Chat.entity.Conversation;
import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface ConversationRepository extends CassandraRepository<Conversation, UUID> {

    @Query("SELECT * FROM conversations WHERE id=?0")
    Conversation getById(UUID id);

    @AllowFiltering
    Conversation findByParticipantIds(List<Long> participantIds);

    @Query("SELECT * FROM conversations WHERE ?0 IN participant_ids GROUP BY id")
    List<Conversation> findByParticipantId(Long participantId);
}