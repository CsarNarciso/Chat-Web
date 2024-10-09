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
                feign.getDetails(participantsIds)
                        .stream()
                        .collect(Collectors.toMap(UserDTO::getId, Function.identity()));

        //Match details with participants
        conversations
                .stream()
                .flatMap(c -> c.getParticipants().stream())
                .forEach(participant -> {
                    mapper.map(participant, details.get(participant.getUserId()));
                });
    }

    @Autowired
    private UserFeign feign;
    @Autowired
    private ConversationService conversationService;
    @Autowired
    private ModelMapper mapper;
}