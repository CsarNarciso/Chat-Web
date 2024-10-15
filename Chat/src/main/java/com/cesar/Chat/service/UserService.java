package com.cesar.Chat.service;

import com.cesar.Chat.dto.ConversationDTO;
import com.cesar.Chat.dto.UserDTO;
import com.cesar.Chat.feign.UserFeign;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@EnableCaching
@Service
public class UserService {

    public void injectConversationsParticipantsDetails(List<ConversationDTO> conversations, List<Long> participantsIds){

        //Fetch details
        Map<Long, UserDTO> details =
                getDetails(participantsIds)
                        .stream()
                        .collect(Collectors.toMap(UserDTO::getId, Function.identity()));

        //Match details with participants
        conversations
                .forEach(c -> {
                    mapper.map(c.getRecipient(), details.get(c.getRecipient().getUserId()));
                });
    }

    public void invalidate(Long id){
        redisTemplate.opsForHash().delete(REDIS_HASH_KEY, id);
    }

    @Cacheable(key = "#", value = REDIS_HASH_KEY)
    private List<UserDTO> getDetails(List<Long> ids){
        return feign.getDetails(ids);
    }

    @Autowired
    private UserFeign feign;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private ModelMapper mapper;

    private final static String REDIS_HASH_KEY = "User";
}