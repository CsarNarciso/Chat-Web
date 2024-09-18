package com.cesar.ConversationAPI.service;

import com.cesar.ConversationAPI.dto.*;
import com.cesar.ConversationAPI.feign.PresenceFeign;
import com.cesar.ConversationAPI.feign.ProfileImageFeign;
import com.cesar.ConversationAPI.feign.UserFeign;
import com.cesar.ConversationAPI.repository.ConversationRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class ConversationService {

    public ConversationDTO createConversation(CreationRequestDTO creationRequest) {

        //Participants data
        List<ParticipantDTO> participants = new ArrayList<>();

        creationRequest.getParticipantsIds().stream().forEach(participantId -> {
            UserDTO user = userFeign.getById(participantId);
            String name = user.getName();
            byte[] profileImage = profileImageFeign.getByPath(user.getProfileImagePath());
            String presenceStatus = presenceFeign.getUserStatus(participantId);

            ParticipantDTO participant = ParticipantDTO
                        .builder()
                        .name(name)
                        .profileImage(profileImage)
                        .presenceStatus(presenceStatus)
                        .build();

            participants.add(participant);
        });

        //Conversation data

        //If it's not One To One...
        String conversationName = creationRequest.isGroup()
                ? creationRequest.getName()
                : participants.stream().filter(p -> p.isSender()).collect(Collectors.toList()).getFirst().getName();

        ConversationDTO conversationData = ConversationDTO
                .builder()
                .name(conversationName)
                .newMessagesAmount(1)
                .participants(participants)
                .build();

        return conversationData;
    }

    @Autowired
    private ConversationRepository repo;
    @Autowired
    private UserFeign userFeign;
    @Autowired
    private ProfileImageFeign profileImageFeign;
    @Autowired
    private PresenceFeign presenceFeign;
    @Autowired
    private ModelMapper mapper;
}