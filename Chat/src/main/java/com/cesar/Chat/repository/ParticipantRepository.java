package com.cesar.Chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cesar.Chat.entity.Participant;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {}