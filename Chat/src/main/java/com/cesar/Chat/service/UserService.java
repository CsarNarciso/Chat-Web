package com.cesar.Chat.service;

import com.cesar.Chat.dto.ConversationDTO;
import com.cesar.Chat.dto.UserDTO;
import com.cesar.Chat.feign.UserFeign;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    private List<UserDTO> getDetails(List<Long> ids){
        return feign.getDetails(ids);
    }



    public UserService(UserFeign feign) {
        this.feign = feign;
    }

    private final UserFeign feign;
    @Autowired
    private ModelMapper mapper;
}