package com.cesar.User.service;

import com.cesar.User.dto.*;
import com.cesar.User.entity.User;
import com.cesar.User.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.EnableCaching;
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

@EnableCaching
@Service
public class UserService {

    public UserDTO create(CreateRequestDTO createRequest, MultipartFile imageMetadata){

        User user = mapper.map(createRequest, User.class);

        //Upload profile image, and store URL in user entity
        String profileImageURL = mediaService.upload(imageMetadata, null);
        user.setProfileImageUrl(profileImageURL);

        //Store in DB
        user = repo.save(user);

        //Store in Cache
        String userKey = generateUserKey(user.getId());
        redisTemplate.opsForValue().set(userKey, user);

        return mapToDTO(user);
    }


    public UserDTO getById(Long id){

        //Try to fetch from Cache
        String userKey = generateUserKey(id);
        User user = (User) redisTemplate.opsForValue().get(userKey);

        //If not in Cache
        if(user==null){

            //Then from DB
            user = repo.getReferenceById(id);

            //And store in Cache
            redisTemplate.opsForValue().set(userKey,user);
        }
        return mapToDTO(user);
    }


    public UserDTO updateDetails(UpdateRequestDTO updateRequest){

        //Update in DB
        User user = repo.save(mapper.map(updateRequest, User.class));

        //Update in Cache
        String userKey = generateUserKey(updateRequest.getId());
        redisTemplate.delete(userKey);
        redisTemplate.opsForValue().set(userKey, user);

        //Event Publisher - User updated
        //when user details (username) is updated
        //Data for: UserDTO{username for Social service}
        return  mapToDTO(user);
    }


    public String updateProfileImage(Long id, MultipartFile imageMetadata, String oldPath){

        //Update in DB
        User user = repo.save(User.builder()
                .id(id)
                .profileImageUrl(mediaService.upload(imageMetadata, oldPath))
                .build());

        //Update in Cache
        String userKey = generateUserKey(id);
        redisTemplate.delete(userKey);
        redisTemplate.opsForValue().set(userKey, user);

        //Event Publisher - User Updated
        //when user image is updated
        //Data: UserDTO{new image url for Social service}
        return user.getProfileImageUrl();
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
        //Data: userId and relationships for Chat and Social services
        return mapToDTO(user);
    }


    public List<UserDTO> getByIds(List<Long> ids){

        //Try to fetch from Cache
        Set<String> userKeys = ids
                .stream()
                .map(this::generateUserKey)
                .collect(Collectors.toSet());

        List<User> users = Objects.requireNonNull(redisTemplate.opsForValue().multiGet(userKeys))
                .stream()
                .map(u -> (User) u)
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
        return String.format("%s", id);
    }


    @Autowired
    private UserRepository repo;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;
    @Autowired
    private MediaService mediaService;
    @Autowired
    private ModelMapper mapper;
}