package com.cesar.Social.service;

import com.cesar.Social.dto.ConversationCreatedDTO;
import com.cesar.Social.dto.ConversationDeletedDTO;
import com.cesar.Social.dto.NetworkDTO;
import com.cesar.Social.entity.Network;
import com.cesar.Social.repository.NetworkRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import java.util.*;

@EnableCaching
@Service
public class NetworkService {

    public NetworkDTO getByUserId(Long userId){

        //Try to fetch from Cache
        String networkKey = generateUserNetworkKey(userId);
        Network network = (Network) redisTemplate.opsForValue().get(networkKey);

        //If not in Cache
        if(network==null){

            //Then from DB
            network = repo.findByUserId(userId);

            //And store in Cache
            redisTemplate.opsForValue().set(networkKey, network);
        }
        return mapper.map(network, NetworkDTO.class);
    }










    @KafkaListener(topics = "ConversationCreated", groupId = "${spring.kafka.consumer.group-id}")
    public void onConversationCreated(ConversationCreatedDTO conversation){

        List<Long> createFor = conversation.getCreateFor();

        //Get references from DB
        List<Network> networks = repo.findByUserIds(createFor);

        Map<String, Network> cacheValues = new HashMap<>();
        Set<String> userNetworkKeys = new HashSet<>();

        //Add new conversation ID to each user network involved on creation
        networks
                .forEach(network -> {

                    network.getConversationIds().add(conversation.getId());

                    String userNetworkKey = generateUserNetworkKey(network.getUserId());
                    cacheValues.put(userNetworkKey, network);
                    userNetworkKeys.add(userNetworkKey);
                });

        //Update in DB
        repo.saveAll(networks);

        //And try to update in Cache
        redisTemplate.delete(userNetworkKeys);
        redisTemplate.opsForValue().multiSet(cacheValues);
    }


    //Event Consumer - ConversationDeleted
    //participantId, conversationId
    public void onConversationDeleted(ConversationDeletedDTO conversation){

        Relationship relationship = repo.findByUserId(conversation.getParticipantId());

        //Remove conversationId from user relationships
        relationship.getConversationIds().removeIf(c -> c.equals(conversation.getConversationId()));
        repo.save(relationship);
    }



    private String generateUserNetworkKey(Long userId){
        return String.format("%s", userId);
    }

    @Autowired
    private NetworkRepository repo;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private ModelMapper mapper;
}