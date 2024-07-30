package com.cesar.ChatWeb.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cesar.ChatWeb.model.Conversation;

import jakarta.transaction.Transactional;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {

	@Query("SELECT c FROM Conversation c WHERE c.senderId = :userId")
	List<Conversation> findAllByUserId(@Param("userId") Long userId);

	@Transactional
	@Modifying
	@Query("UPDATE Conversation c SET c.name = :newName WHERE c.recipientId = :userId")
	void updateNameByUserId(@Param("userId") Long userId, @Param("newName") String newName);

	@Transactional
	@Modifying
	@Query("UPDATE Conversation c SET c.imageName= :newImageName WHERE c.recipientId = :userId")
	void updateImageNameByUserId(@Param("userId") Long userId, @Param("newImageName") String newImageName);

	@Transactional
	@Modifying
	@Query("UPDATE Conversation c SET c.newMessagesAmount = :newMessagesAmount WHERE c.senderId = :senderId AND c.recipientId = :recipientId")
	void updateNewMessagesByIds(@Param("senderId") Long senderId, @Param("recipientId") Long recipientId, @Param("newMessagesAmount") int newMessagesAmount);

}
