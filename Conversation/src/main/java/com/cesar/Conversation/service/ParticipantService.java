package com.cesar.Conversation.service;

import com.cesar.Conversation.entity.Participant;
import com.cesar.Conversation.repository.ParticipantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ParticipantService {

    public void setInitialConversationUnreadMessages(Long senderId, List<Participant> participants){
        participants
            .forEach(participant -> {
                //Sender doesn't increment
                if(!participant.getUserId().equals(senderId)){
                    participant.increaseUnreadMessage();
                }
            });
    }
    public void increaseUnreadMessages(Long userId){
        repo.increaseUnreadMessages(userId);
    }
    public void cleanUnreadMessages(Long userId){
        repo.cleanUnreadMessages(userId);
    }
    @Autowired
    private ParticipantRepository repo;
}