package com.cesar.Chat.service;

import java.util.List;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import com.cesar.Chat.dto.ConversationDTO;
import com.cesar.Chat.entity.Conversation;
import com.cesar.Chat.repository.ConversationRepository;

@Service
@CacheConfig(cacheNames = "userConversations")
public class ConversationDataService {

	@Caching(evict = {
			@CacheEvict(key = "#senderId"),
			@CacheEvict(key = "#recipientId")
	})
    public Conversation save(Conversation conversation, Long senderId, Long recipientId){
		return repo.save(conversation);
    }
	
	@Cacheable(key = "#userId", unless = "#result == null or #result.size() == 0")
    public List<ConversationDTO> getAllByUserId(Long userId){
    	return mapToDTOs(repo.findByUserId(userId));
    }
	
	public Conversation getById(UUID id){
        return repo.findById(id).orElse(null);
    }
	
    public Conversation matchByUserIds(List<Long> userIds, int userCount){
        return repo.findByUserIds(userIds, userCount);
    }

    @CacheEvict(key = "#userId")
    public void delete(UUID id, Long userId){
    	repo.deleteById(id);
    }
    
    public void disableOnUserDeleted(Long userId) {
    	repo.disableOnUserDeleted(userId);
    }
	
    
    
	private List<ConversationDTO> mapToDTOs(List<Conversation> conversations){
		return conversations
		        .stream()
		        .map(c -> mapper.map(c, ConversationDTO.class))
		        .toList();
	}
	
	
	
	public ConversationDataService(ConversationRepository repo, ModelMapper mapper) {
		this.repo = repo;
		this.mapper = mapper;
	}
	
	private final ConversationRepository repo;
	private final ModelMapper mapper;
}