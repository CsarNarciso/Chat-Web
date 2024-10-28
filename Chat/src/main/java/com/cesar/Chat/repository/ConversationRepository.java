package com.cesar.Chat.repository;

import com.cesar.Chat.entity.Conversation;
import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import java.util.List;
import java.util.UUID;

public interface ConversationRepository extends CassandraRepository<Conversation, UUID> {

    @AllowFiltering
    Conversation findByParticipantIds(List<Long> participantIds);

    @Query("SELECT * FROM conversations WHERE ?0 IN participant_ids")
    List<Conversation> findByParticipantId(Long participantId);
}