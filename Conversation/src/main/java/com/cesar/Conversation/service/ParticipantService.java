package com.cesar.Conversation.service;

import com.cesar.Conversation.dto.UpdateParticipantDTO;
import com.cesar.Conversation.entity.Participant;
import com.cesar.Conversation.repository.ParticipantRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class ParticipantService {

    public List<Participant> getParticipantsDetails(List<Long> participantsIds){
        return userService.getUsersDetails(participantsIds)
                .stream()
                .map(user -> mapper.map(user, Participant.class))
                .toList();
    }

    public void updateUserDetails(UpdateParticipantDTO participant){
        repo.save(
                mapper.map(participant, Participant.class)
        );
    }
    public void setUnreadMessagesInOne(List<Long> participantsIds){
        List<Participant> participants = new ArrayList<>();
        participantsIds
                .forEach(participantId -> {
                    participants.add(Participant
                            .builder()
                            .id(participantId)
                            .unreadMessages(1)
                            .build());
                });
        repo.saveAll(participants);
    }
    public void increaseUnreadMessages(Long senderId, Long conversationId){
        repo.increaseUnreadMessages(senderId, conversationId);
    }
    public void cleanUnreadMessages(Long participantId){
        repo.save(
                Participant
                        .builder()
                        .id(participantId)
                        .unreadMessages(0)
                        .build()
        );
    }
    @Autowired
    private ParticipantRepository repo;
    @Autowired
    private UserService userService;
    @Autowired
    private ModelMapper mapper;
}