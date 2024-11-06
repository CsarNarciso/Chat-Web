package com.cesar.User.service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.multipart.MultipartFile;

import com.cesar.User.dto.CreateRequestDTO;
import com.cesar.User.dto.UpdateRequestDTO;
import com.cesar.User.dto.UserDTO;
import com.cesar.User.entity.User;
import com.cesar.User.repository.UserRepository;

@Service
public class UserService {


    public UserDTO create(CreateRequestDTO createRequest){

        User entity = mapper.map(createRequest, User.class);

        //Upload profile image, and store URL in user entity
        String profileImageURL = mediaService.upload(createRequest.getImageMetadata(), null);
        entity.setProfileImageUrl(profileImageURL);

        //Store in DB
        entity = repo.save(entity);
        UserDTO user = mapToDTO(entity);

        //Store in Cache
        String userKey = generateUserKey(user.getId());
        redisTemplate.opsForValue().set(userKey, user);

        return user;
    }
    
     
    public UserDTO getById(Long id){

    	UserDTO user = new UserDTO();
        
    	//Try to fetch from Cache
        String userKey = generateUserKey(id);
        UserDTO cacheUser = redisTemplate.opsForValue().get(userKey);
        
        //If not missing Cache
        if(cacheUser!=null){
            user = cacheUser;
        }
        else {
        	 //Then from DB
            User entity = repo.findById(id).orElse(null);
            
            //And store in Cache
            if(entity==null) {            	
            	return null;
            }
            user = mapToDTO(entity);
            redisTemplate.opsForValue().set(userKey, user);
        }
        return user;
    }


    public List<UserDTO> getByIds(List<Long> ids){

        //Try to fetch from Cache
        Set<String> userKeys = ids
                .stream()
                .map(this::generateUserKey)
                .collect(Collectors.toSet());

        List<UserDTO> users = new ArrayList<>(redisTemplate.opsForValue().multiGet(userKeys)
        		.stream()
        		.filter(Objects::nonNull)
        		.toList());
        
        //If missing cache
        List<Long> cacheUserIds = users
        		.stream()
                .map(UserDTO::getId)
                .toList();
        
        List<Long> missingCacheIds = ids
        		.stream()
        		.filter(id -> !cacheUserIds.contains(id))
        		.toList();        

        if(!missingCacheIds.isEmpty()){

            //Get from DB
            List<User> missingUsers = repo.findAllById(missingCacheIds);
            
            if(!missingUsers.isEmpty()) {
            	
            	users.addAll(mapToDTOS(missingUsers));
            	
            	//And store in Cache
                Map<String, UserDTO> missingUserKeys = missingUsers
                        .stream()
                        .map( u -> mapper.map(u,  UserDTO.class) )
                        .collect(Collectors.toMap(
                                u -> generateUserKey(u.getId()),
                                Function.identity()));
                redisTemplate.opsForValue().multiSet(missingUserKeys);
            }
        }
        return users;
    }


    public UserDTO updateDetails(Long id, UpdateRequestDTO updateRequest){
    	
    	//If user exists...
    	User existentEntity = repo.findById(id).orElse(null); 
    	
    	if(existentEntity!=null) {
    		
    		boolean updatePerformed = false;
    		User entityForUpdate = mapper.map(existentEntity, User.class);
    		
    		//For each allowed update request field
			for (Field updateRequestField : UpdateRequestDTO.class.getDeclaredFields()) {
				
				//Make private request DTO field accessible
				updateRequestField.setAccessible(true);
				
				//If it's present on request (either null or not)
				Optional<?> updateRequestFieldValue = Optional.empty();
				
				try {
					updateRequestFieldValue = (Optional<?>) updateRequestField.get(updateRequest);
				} catch (IllegalArgumentException | IllegalAccessException e) {e.printStackTrace();}
				
				if(updateRequestFieldValue.isPresent()) {
					
					//Get field (map) from DTO to Entity
					Field entityField = ReflectionUtils.findField(User.class, updateRequestField.getName());
								
					//And set (update) on entity
					try {
						
						entityField.setAccessible(true);
						entityField.set(entityForUpdate, updateRequestFieldValue.get());
						updatePerformed = true;
						
					} catch (IllegalArgumentException | IllegalAccessException e) {e.printStackTrace();}
				}
			}
			UserDTO user = mapToDTO(existentEntity);
			
			//If at least a field update was made
			if(updatePerformed) {
				
				//Update in DB
				user = mapToDTO(repo.save(entityForUpdate));
				
		        //Update in Cache
		        String userKey = generateUserKey(user.getId());
		        redisTemplate.opsForValue().set(userKey, user);

		        //Event Publisher - User updated
		        kafkaTemplate.send("UserUpdated", user);
			}
			return user;
    	}
    	return null;
    }
    
		
		

    public String updateProfileImage(Long id, MultipartFile imageMetadata, String oldPath){

        String newImageUrl = mediaService.upload(imageMetadata, oldPath);

        //If either new image is not empty (bad arguments request),
        // or media service request was performed successful (no fallback)
        if(newImageUrl!=null && !newImageUrl.equals(oldPath)){

            //Update in DB
            User entity = repo.save(User.builder()
                    .id(id)
                    .profileImageUrl(newImageUrl)
                    .build());
            
            UserDTO user = mapToDTO(entity);

            //Update in Cache
            String userKey = generateUserKey(id);
            redisTemplate.opsForValue().set(userKey, user);

            //Event Publisher - User Updated
            kafkaTemplate.send("UserUpdated", user);

            return entity.getProfileImageUrl();
        }
        return null;
    }


    public UserDTO delete(Long id){

        User user = repo.getReferenceById(id);

        //If exists...
        if(user!=null){

            //Remove Profile Image from Media Server
            mediaService.delete(user.getProfileImageUrl());

            //Delete in DB
            repo.deleteById(id);

            //And invalidate in Cache
            String userKey = generateUserKey(id);
            redisTemplate.delete(userKey);
        }

        //Event Publisher - User Deleted
        kafkaTemplate.send("UserDeleted", id);

        return mapToDTO(user);
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

    private String generateUserKey(Long id){
        return String.format("user:%s", id);
    }



    public UserService(UserRepository repo, RedisTemplate<String, UserDTO> redisTemplate, KafkaTemplate<String, Object> kafkaTemplate, MediaService mediaService, ModelMapper mapper) {
        this.repo = repo;
        this.redisTemplate = redisTemplate;
        this.kafkaTemplate = kafkaTemplate;
        this.mediaService = mediaService;
        this.mapper = mapper;
    }

    private final UserRepository repo;
    private final RedisTemplate<String, UserDTO> redisTemplate;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final MediaService mediaService;
    private final ModelMapper mapper;
}