package com.cesar.User.service;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.cesar.User.dto.UserDTO;
import com.cesar.User.entity.User;
import com.cesar.User.repository.UserRepository;

@Service
@CacheConfig(cacheNames = "users")
public class UserDataService {

	@CachePut(key = "#result.id")
    public UserDTO save(User user){
        return mapToDTO(repo.save(user));
    }
    
    @Cacheable(key = "#id", unless = "#result == null")
    public UserDTO getById(Long id){
		User entity = repo.findById(id).orElse(null);
		return entity!=null 
				? mapToDTO(entity) 
				: null; 
    }

    public List<UserDTO> getByIds(List<Long> ids){
		List<User> entities = repo.findAllById(ids);
		return !entities.isEmpty()
					? mapToDTOS(entities)
					: null;
    }
	
	@CacheEvict(key = "#id")
	public void delete(Long id){
		repo.deleteById(id);
	}



    private UserDTO mapToDTO(User user){
        return mapper.map(user, UserDTO.class);
    }

    private List<UserDTO> mapToDTOS(List<User> users){
        return users
                .stream()
                .map(u -> mapper.map(u, UserDTO.class))
                .toList();
    }


    public UserDataService(UserRepository repo, ModelMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    private final UserRepository repo;
    private final ModelMapper mapper;
}