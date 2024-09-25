package com.cesar.Conversation.service;

import com.cesar.Conversation.entity.Participant;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ParticipantService {

    public void incrementNewUnreadMessage(Long senderId, List<Participant> participants){
        participants
            .forEach(participant -> {
                //Sender doesn't increment
                if(!participant.getUserId().equals(senderId)){
                    participant.incrementUnreadMessage();
                }
            });
    }
}