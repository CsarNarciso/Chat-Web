package com.cesar.Conversation.service;

import com.cesar.Conversation.entity.Participant;
import com.cesar.Conversation.repository.ParticipantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class ParticipantService {

    public List<Long> getIdsByUserIds(List<Long> usersIds){
        return repo.findIdByUserId(usersIds);
    }
    public void updateUnreadMessages(List<Long> participantsIds){
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
        repo.cleanUnreadMessages(participantId);
    }

    @Autowired
    private ParticipantRepository repo;
}