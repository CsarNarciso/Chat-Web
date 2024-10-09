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

    public void injectConversationsParticipantsDetails(List<ConversationDTO> conversations){
        //Fetch details
        Map<Long, UserDTO> details =
                feign.getDetails(conversationService.getConversationsParticipantsIds(conversations))
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

    public List<UserDTO> getUsersDetails(List<Long> usersIds) {
        return feign.getDetails(usersIds);
    }
    @Autowired
    private UserFeign feign;
    @Autowired
    private ConversationService conversationService;
    @Autowired
    private ModelMapper mapper;
}