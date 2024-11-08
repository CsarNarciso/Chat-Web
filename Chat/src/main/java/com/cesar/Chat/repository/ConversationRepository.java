package com.cesar.Chat.repository;

import com.cesar.Chat.entity.Conversation;
import com.cesar.Chat.entity.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, UUID> {

	@Query("SELECT c FROM Conversation c "
			+ "JOIN c.participants p "
			+ "WHERE p IN :userIds "
			+ "GROUP BY c.id "
			+ "HAVING COUNT(p.id) = :userCount")
	Conversation findByUserIds(@Param("userIds") List<Long> userIds, @Param("userCount") int userCount);

    @Query("SELECT c FROM Conversation c " +
            "JOIN c.participants p " +
            "WHERE p.id = :userId ")
    List<Conversation> findByUserId(@Param("userId") Long userId);
}