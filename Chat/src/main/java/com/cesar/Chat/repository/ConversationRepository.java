package com.cesar.Chat.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.cesar.Chat.entity.Conversation;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, UUID> {

	@Query(value="""
			SELECT c.* FROM conversations c 
			JOIN conversations_participants cp ON c.id = cp.conversation_id
			WHERE cp.user_id IN :userIds 
			AND cp.user_id NOT MEMBER OF c.recreateFor
			GROUP BY c.id 
			HAVING COUNT(DISTINCT cp.user_id) = :userCount
			""", nativeQuery=true)
	Conversation findByUserIds(@Param("userIds") List<Long> userIds, @Param("userCount") int userCount);

    @Query("SELECT c FROM Conversation c WHERE :userId MEMBER OF c.participants AND :userId NOT MEMBER OF c.recreateFor")
    List<Conversation> findByUserId(@Param("userId") Long userId);
}