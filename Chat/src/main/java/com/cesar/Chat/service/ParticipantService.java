package com.cesar.Chat.service;

import java.util.List;

import org.springframework.stereotype.Service;
import com.cesar.Chat.entity.Conversation;
import com.cesar.Chat.entity.Participant;
import com.cesar.Chat.repository.ParticipantRepository;

@Service
public class ParticipantService {
	
	public Participant create(Long userId, Conversation conversation) {
		return repo.save(
				Participant
					.builder()
					.userId(userId)
					.conversation(conversation)
					.build());
	}
	
	public List<Participant> createAll(List<Long> userIds, Conversation conversation) {
		return repo.saveAll(
				userIds
					.stream()
					.map(userId -> 
						Participant
							.builder()
							.userId(userId)
							.conversation(conversation)
							.build())
					.toList());
	}
	
	public List<Participant> getByUserIds(List<Long> userIds){
		return repo.findAllById(userIds);
	}
	
	
	
	public ParticipantService(ParticipantRepository repo) {
		this.repo = repo;
	}
	
	private final ParticipantRepository repo;
}