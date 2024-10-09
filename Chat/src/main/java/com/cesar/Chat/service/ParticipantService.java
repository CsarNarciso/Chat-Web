package com.cesar.Chat.service;

import com.cesar.Chat.entity.Participant;
import com.cesar.Chat.repository.ParticipantRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class ParticipantService {

    public List<Participant> createInBatch(List<Long> usersIds){
        List<Participant> participants = new ArrayList<>();
        usersIds
                .forEach(id -> {
                    participants.add(
                            Participant
                                    .builder()
                                    .userId(id)
                                    .build()
                    );
                });
        return participants;
    }

    public List<Participant> getParticipantsDetails(List<Long> participantsIds){
        return userService.getUsersDetails(participantsIds)
                .stream()
                .map(user -> mapper.map(user, Participant.class))
                .toList();
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