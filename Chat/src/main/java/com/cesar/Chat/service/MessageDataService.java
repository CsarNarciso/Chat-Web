package com.cesar.Chat.service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.cesar.Chat.dto.MessageDTO;
import com.cesar.Chat.dto.UnreadMessagesDTO;
import com.cesar.Chat.entity.Message;
import com.cesar.Chat.repository.MessageRepository;

@Service
@CacheConfig(cacheNames = "conversationMessages")
public class MessageDataService {
	
	@CacheEvict(key = "#conversationId")
	public MessageDTO save(Message message, UUID conversationId) {
		return mapToDTO(repo.save(message));
	}
	
	@Cacheable(key = "#conversationId")
	public List<MessageDTO> getAllByConversationId(UUID conversationId) {
        return mapToDTOs(repo.findAllByConversationId(conversationId));
    }
	
	public List<Message> getAllByConversationIds(List<UUID> conversationIds) {
        return repo.findAllByConversationIds(conversationIds);
    }
	
	public List<Message> getLastMessages(Long participantId, List<UUID> conversationIds){
		return repo.getLastMessages(participantId, conversationIds);
	}
	
	public List<UnreadMessagesDTO> getUnreadMessages(Long participantId, List<UUID> conversationIds){
		return repo.getUnreadMessages(participantId, conversationIds);
	}
	
	public void markMessagesAsRead(UUID conversationId, Long participantId) {
    	repo.markMessagesAsRead(participantId, conversationId);
    }

    public void deleteByUserId(Long userId) {
    	repo.deleteBySenderId(userId);
    }
	
	
	
	public MessageDTO mapToDTO(Message message){
        return mapper.map(message, MessageDTO.class);
    }
	
	private List<MessageDTO> mapToDTOs(List<Message> messages){
        return messages
                .stream()
                .filter(Objects::nonNull)
                .map(m -> mapper.map(m, MessageDTO.class))
                .toList();
    }
	
	
	public MessageDataService(MessageRepository repo, ModelMapper mapper) {
		this.repo = repo;
		this.mapper = mapper;
	}
	
    private final MessageRepository repo;
    private final ModelMapper mapper;
}
