package com.cesar.Chat.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.cesar.Chat.dto.ParticipantDTO;
import com.cesar.Chat.entity.Participant;
import com.cesar.Chat.repository.ParticipantRepository;

@Service
public class ParticipantService {
	
	public List<Participant> createAll(List<Long> userIds) {
		
		//Create references
		List<Participant> participants = userIds
						.stream()
						.map(userId -> 
							Participant
								.builder()
								.userId(userId)
								.build())
						.toList();		
		return participants;
	}
	
	public void cacheAll(List<Participant> participants, UUID conversationId) {
		
		//Store in Cache
		String conversationParticipantsKey = generateConversationParticipantsKey(conversationId); 
		
		Map<String, ParticipantDTO> cacheable = participants
				.stream()
				.map(p -> mapper.map(p, ParticipantDTO.class))
				.collect(Collectors.toMap(p -> p.getUserId().toString(), Function.identity()));
		
		redisTemplate.opsForHash().putAll(conversationParticipantsKey, cacheable);
	}
	
	public List<Participant> getByUserIds(List<Long> userIds){
		return repo.findAllById(userIds);
	}
	
	
	
	
	
	
	private String generateConversationParticipantsKey(UUID conversationId) {
		return String.format("conversation:%s:participants", conversationId);
	}
	
	
	public ParticipantService(ParticipantRepository repo, RedisTemplate<String, ParticipantDTO> redisTemplate, ModelMapper mapper) {
		this.repo = repo;
		this.redisTemplate = redisTemplate;
		this.mapper = mapper;
	}
	
	private final ParticipantRepository repo;
    private final RedisTemplate<String, ParticipantDTO> redisTemplate;
    private final ModelMapper mapper;
}