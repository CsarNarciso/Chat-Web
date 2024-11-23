package com.cesar.User.service;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.cesar.User.dto.UserDTO;
import com.cesar.User.entity.User;
import com.cesar.User.repository.UserRepository;

@Service
@CacheConfig(cacheNames = "users")
public class UserDataService {

	@CacheEvict(key = "#result.id")
    public UserDTO save(User user){
        return mapToDTO(repo.save(user));
    }
    
    @Cacheable(key = "#id", unless = "#result == null")
    public UserDTO getById(Long id){
    	
		User entity = repo.findById(id).orElse(null);
		if(entity!=null) {
			
			//If deleted
			UserDTO user = mapToDTO(entity);
			if(user.isDeleted()) {
				setDefaultDeletedUserDetails(user);
			}
			return user;
		}
		return null;
    }

    public List<UserDTO> getByIds(List<Long> ids){
    	
		List<User> entities = repo.findAllById(ids);
		if(!entities.isEmpty()) {
			
			//If deleted users
			List<UserDTO> users = mapToDTOS(entities);
			setDefaultDeletedUsersDetails(users);
			
			return users;
		}
		return null;
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
    
    private void setDefaultDeletedUserDetails(UserDTO user) {
    	user.setUsername(DELETED_USERNAME);
		user.setProfileImageUrl(DEFAULT_IMAGE_URL);
    }
    
    private void setDefaultDeletedUsersDetails(List<UserDTO> users) {
    	users
		.forEach(user -> {
			if(user.isDeleted()) {
				setDefaultDeletedUserDetails(user);
			}
		});
    }



    public UserDataService(UserRepository repo, ModelMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    private final UserRepository repo;
    private final ModelMapper mapper;
    @Value("${defaultImage.url}")
    private String DEFAULT_IMAGE_URL;
    @Value("${default.deleted.username}")
    private String DELETED_USERNAME;
}