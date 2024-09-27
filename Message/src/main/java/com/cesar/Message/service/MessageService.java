package com.cesar.Message.service;

import com.cesar.Message.dto.FirstMessageDTO;
import com.cesar.Message.dto.MessageDTO;
import com.cesar.Message.entity.Message;
import com.cesar.Message.repository.MessageRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MessageService {

    //Event Consumer - New Conversation created
    //when conversation service create new one after first message is sent
    //Data: FirstMessageDTO, conversationId
    public void sendFirstInteractionMessage(FirstMessageDTO firstMessage) {
        //Get event data
        Long newConversationId=1L; //event.get("conversationId")
        //Store in CACHE, after TTL ends, store directly in DB
        //For the moment, store in DB
        Message entity = mapper.map(firstMessage, Message.class);
        entity.setConversationId(newConversationId);
        repo.save(entity);
    }

    public void send(MessageDTO message) {
        //Store in CACHE with TTL, after TTL or offline user event, store in DB
        //For the moment, Store in DB
        Message entity = mapper.map(message, Message.class);
        repo.save(entity);
        //Event publisher - notify new unread message
        //when new message is stored in DB
        //Data: message content, conversationId
    }

    public List<MessageDTO> loadConversationMessages(Long conversationId){
        //Get from CACHE, if they are not stored, get from DB and store in CACHE with TTL
        //For the moment, get from DB
        return repo.findByConversationId(conversationId)
                .stream()
                .map(m -> mapper.map(m, MessageDTO.class))
                .toList();
    }

    //Event Consumer - Offline user
    //when presence service notifies user as offline
    //Task: store all new user data in CACHE into DB
    //Data: userId
    public void storeFromCacheIntoDB(Long userId){
        //Get new messages from CACHE
        //Store them in DB
    }

    @Autowired
    private MessageRepository repo;
    @Autowired
    private ModelMapper mapper;
}