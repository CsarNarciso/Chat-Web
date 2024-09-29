package com.cesar.Conversation.service;

import com.cesar.Conversation.entity.Participant;
import com.cesar.Conversation.feign.UserFeign;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserService {

    public List<Participant> getParticipantsDetails(List<Long> participantsIds) {
        return userFeign.getDetails(participantsIds)
                .stream()
                .map(user -> mapper.map(user, Participant.class))
                .toList();
    }
    @Autowired
    private UserFeign userFeign;
    @Autowired
    private ModelMapper mapper;
}