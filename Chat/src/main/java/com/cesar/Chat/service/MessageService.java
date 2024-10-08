package com.cesar.Chat.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageService {

    public void send(SendRequestDTO sendRequest) {

        Message entity = mapper.map(sendRequest, Message.class);
        entity.setSentAt(LocalDateTime.now());

        //If message doesn't contain conversation ID
        //(either conversation doesn't exist yet, or sender has deleted it for itself)
        if(sendRequest.getConversationId()==null){
            //Ask Conversation Service to create/recreate it and get id
            Long newConversationId = conversationService.create(sendRequest);
            sendRequest.setConversationId(newConversationId);
            //Store in DB
            repo.save(entity);
        }
        else{
            //Store in DB
            MessageDTO message = mapper.map(repo.save(entity), MessageDTO.class);
            //Publish Event - MessageSent
            //when new message is sent for an existent conversation
            //Data for: message data for WS and Conversation Service
        }
    }

    public List<MessageDTO> loadConversationMessages(Long conversationId){
        //Get from CACHE, if they are not stored, get from DB and store in CACHE with TTL
        //For the moment, get from DB
        return repo.findByConversationId(conversationId)
                .stream()
                .map(m -> mapper.map(m, MessageDTO.class))
                .toList();
    }

    @Autowired
    private MessageRepository repo;
    @Autowired
    private ConversationService conversationService;
    @Autowired
    private ModelMapper mapper;
}