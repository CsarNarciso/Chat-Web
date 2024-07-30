package com.cesar.ChatWeb.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.cesar.ChatWeb.model.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

	@Query("SELECT m FROM Message m WHERE (m.senderId = :senderId OR m.senderId = :recipientId) AND (m.recipientId = :recipientId OR m.recipientId = :senderId) ORDER BY m.date ASC")
	List<Message> findAllByIds(@Param("senderId") Long senderId, @Param("recipientId") Long recipientId);
}
