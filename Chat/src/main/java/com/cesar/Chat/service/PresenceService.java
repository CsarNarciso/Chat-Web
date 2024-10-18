package com.cesar.Chat.service;

import com.cesar.Chat.dto.ConversationDTO;
import com.cesar.Chat.dto.ParticipantDTO;
import com.cesar.Chat.dto.UserPresenceDTO;
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

    public void injectConversationsParticipantsStatuses(List<ConversationDTO> conversations,
                                                        List<Long> participantsIds){
        //Fetch presence statuses
        Map<Long, UserPresenceDTO> statuses =
                feign.getByUsersIds(participantsIds)
                        .stream()
                        .collect(Collectors.toMap(UserPresenceDTO::getId, Function.identity()));

        //Match statuses with participants
        conversations
                .forEach(c -> {
                    mapper.map(c.getRecipient(), statuses.get(c.getRecipient().getUserId()));
                });
    }

    @Autowired
    private PresenceFeign feign;
    @Autowired
    private ConversationService conversationService;
    @Autowired
    private ModelMapper mapper;
}