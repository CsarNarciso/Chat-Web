package com.cesar.User.service;

import com.cesar.User.dto.*;
import com.cesar.User.entity.User;
import com.cesar.User.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class UserService {


    public UserDTO create(CreateRequestDTO createRequest){

        User user = mapper.map(createRequest, User.class);

        //Upload profile image, and store URL in user entity
        String profileImageURL = mediaService.upload(createRequest.getImageMetadata(), null);
        user.setProfileImageUrl(profileImageURL);

        //Store in DB
        user = repo.save(user);

        //Store in Cache
        String userKey = generateUserKey(user.getId());
        redisTemplate.opsForValue().set(userKey, user);

        return mapToDTO(user);
    }
    
     
    public UserDTO getById(Long id){

    	User user = new User();
        
    	//Try to fetch from Cache
        String userKey = generateUserKey(id);
        User cacheUser = redisTemplate.opsForValue().get(userKey);
        
        //If not missing Cache
        if(cacheUser!=null){
            user = cacheUser;
        }
        else {
        	 //Then from DB
            user = repo.findById(id).orElse(null);
            
            //And store in Cache
            if(user==null) {            	
            	return null;
            }
            redisTemplate.opsForValue().set(userKey, user);
        }
        return mapToDTO(user);
    }


    public List<UserDTO> getByIds(List<Long> ids){

        //Try to fetch from Cache
        Set<String> userKeys = ids
                .stream()
                .map(this::generateUserKey)
                .collect(Collectors.toSet());

        List<User> users = redisTemplate.opsForValue().multiGet(userKeys)
        		.stream()
        		.filter(Objects::nonNull)
        		.toList();
        
        //If missing cache
        List<Long> missingCacheIds = users
                .stream()
                .filter(u -> !ids.contains(u.getId()))
                .map(User::getId)
                .toList();

        if(!missingCacheIds.isEmpty()){

            //Get from DB
            List<User> missingUsers = repo.findAllById(missingCacheIds);
            users.addAll(missingUsers);

            //And store in Cache
            Map<String, User> missingUserKeys = missingUsers
                    .stream()
                    .collect(Collectors.toMap(
                            u -> generateUserKey(u.getId()),
                            Function.identity()));
            redisTemplate.opsForValue().multiSet(missingUserKeys);
        }
        return mapToDTOS(users);
    }



    public UserDTO updateDetails(UpdateRequestDTO updateRequest){

        //Update in DB
        User user = repo.save(mapper.map(updateRequest, User.class));
        UserDTO dto = mapToDTO(user);

        //Update in Cache
        String userKey = generateUserKey(updateRequest.getId());
        redisTemplate.opsForValue().set(userKey, user);

        //Event Publisher - User updated
        kafkaTemplate.send("UserUpdated", dto);

        return dto;
    }


    public String updateProfileImage(Long id, MultipartFile imageMetadata, String oldPath){

        String newImageUrl = mediaService.upload(imageMetadata, oldPath);

        //If either new image is not empty (bad arguments request),
        // or media service request was performed successful (no fallback)
        if(newImageUrl!=null && !newImageUrl.equals(oldPath)){

            //Update in DB
            User user = repo.save(User.builder()
                    .id(id)
                    .profileImageUrl(newImageUrl)
                    .build());

            //Update in Cache
            String userKey = generateUserKey(id);
            redisTemplate.opsForValue().set(userKey, user);

            //Event Publisher - User Updated
            kafkaTemplate.send("UserUpdated", mapToDTO(user));

            return user.getProfileImageUrl();
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



    public UserService(UserRepository repo, RedisTemplate<String, User> redisTemplate, KafkaTemplate<String, Object> kafkaTemplate, MediaService mediaService, ModelMapper mapper) {
        this.repo = repo;
        this.redisTemplate = redisTemplate;
        this.kafkaTemplate = kafkaTemplate;
        this.mediaService = mediaService;
        this.mapper = mapper;
    }

    private final UserRepository repo;
    private final RedisTemplate<String, User> redisTemplate;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final MediaService mediaService;
    private final ModelMapper mapper;
}