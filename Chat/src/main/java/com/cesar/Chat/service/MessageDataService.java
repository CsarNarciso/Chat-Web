package com.cesar.Chat.service;

import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import com.cesar.Chat.dto.MessageDTO;
import com.cesar.Chat.entity.Message;
import com.cesar.Chat.repository.MessageRepository;

@Service
public class MessageDataService {
	
	@Cacheable(value = "conversationMessages", key = "#conversationId")
	public MessageDTO save(Message message, UUID conversationId) {
		return mapToDTO(repo.save(message));
	}
	
	@Cacheable(value = "conversationUserUnreadMessages", key = "'#conversationId' + '#recipientId'")
	public void incrementUnreadMessages(UUID conversationId, Long recipientId) {}
	
	
	
	
	public MessageDTO mapToDTO(Message message){
        return mapper.map(message, MessageDTO.class);
    }
	
	
	public MessageDataService(MessageRepository repo, ModelMapper mapper) {
		this.repo = repo;
		this.mapper = mapper;
	}
	
    private final MessageRepository repo;
    private final ModelMapper mapper;
}
