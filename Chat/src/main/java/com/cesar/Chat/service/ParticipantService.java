package com.cesar.Chat.service;

import java.util.List;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ListOperations;
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
		
		//Store in DB
		List<Participant> participants = repo.saveAll(
					userIds
						.stream()
						.map(userId -> 
							Participant
								.builder()
								.userId(userId)
								.conversation(conversation)
								.build())
						.toList());
		//Store in Cache
		redisTemplate.opsForList().rightPush(generateConversationParticipantsKey(), participants);
	}
	
	public List<Participant> getByUserIds(List<Long> userIds){
		return repo.findAllById(userIds);
	}
	
	
	
	public ParticipantService(ParticipantRepository repo, RedisTemplate<String, ParticipantDTO> redisTemplate) {
		this.repo = repo;
		this.redisListTemplate = redisTemplate.opsForList();
	}
	
	private final ParticipantRepository repo
    private final ListOperations<String, ParticipantDTO> redisListTemplate;
}