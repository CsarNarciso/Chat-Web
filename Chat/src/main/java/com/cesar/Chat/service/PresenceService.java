package com.cesar.Chat.service;

import com.cesar.Chat.dto.ConversationDTO;
import com.cesar.Chat.dto.ParticipantDTO;
import com.cesar.Chat.dto.PresenceStatusDTO;
import com.cesar.Chat.feign.PresenceFeign;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PresenceService {

    public void injectConversationsParticipantsStatuses(List<ConversationDTO> conversations){

        //Get participants Ids
        List<Long> participantsUsersIds = conversations
                .stream()
                .flatMap(c -> c.getParticipants().stream())
                .map(ParticipantDTO::getUserId).collect(Collectors.toList());

        //Fetch presence statuses
        Map<Long, PresenceStatusDTO> statuses =
                presenceFeign.getStatuses(participantsUsersIds)
                        .stream()
                        .collect(Collectors.toMap(PresenceStatusDTO::getUserId, Function.identity()));

        //Match statuses with participants
        conversations
                .stream()
                .flatMap(c -> c.getParticipants().stream())
                .forEach(participant -> {
                    PresenceStatusDTO status = statuses.get(participant.getUserId());
                    if(status != null) {
                        mapper.map(participant, status);
                    }
                });
    }

    @Autowired
    private PresenceFeign presenceFeign;
    @Autowired
    private ModelMapper mapper;
}