package com.cesar.Chat.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
public class MessageService {

    public void send(CreationRequestDTO message) {

        List<Long> participantsIds = Stream.of(message.getSenderId(), message.getRecipientId()).toList();

        //If message has no conversation ID
        // (either conversation doesn't exist yet, or sender has deleted it for itself)
        if(message.getConversationId()==null){

            //Verify if conversation between both users exists (IN CACHE):
            //by checking if a conversation has those users ids in participants attribute
            // If not...
            //Request Conversation service to create new one and
            //get new conversation id
            Long conversationId = conversationService.create(participantsIds);

            //If it exists...
            //Get conversation id
            // and recreate for sender in next step (along with other participants in recreateFor attribute)
            message.setConversationId(conversationId);

        } //If Conversation exists...
        else{
            //Check by conversationId, if in CACHE existentConversations, there is something in recreateFor
            //attribute (participantsIds), if it does:
            //Request Conversation Service to recreate conversation for those users
            conversationService.recreate(message.getConversationId(), List.of(message.getRecipientId()));
        }

        //Store message in CACHE, after certain time, store in DB
        //For the moment, store in DB
        Message entity = mapper.map(message, Message.class);
        repo.save(entity);
        //Send message:
        //Event Publisher - New message
        //when new message is stored in DB/CACHE
        //Data for: message data (DTO) for WS Server and Conversation Service
        mapper.map(entity, MessageDTO.class);
    }

    public List<CreationRequestDTO> loadConversationMessages(Long conversationId){
        //Get from CACHE, if they are not stored, get from DB and store in CACHE with TTL
        //For the moment, get from DB
        return repo.findByConversationId(conversationId)
                .stream()
                .map(m -> mapper.map(m, CreationRequestDTO.class))
                .toList();
    }

    @Autowired
    private MessageRepository repo;
    @Autowired
    private ConversationService conversationService;
    @Autowired
    private ModelMapper mapper;
}