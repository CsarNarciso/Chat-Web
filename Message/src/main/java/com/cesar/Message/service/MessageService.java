package com.cesar.Message.service;

import com.cesar.Message.dto.MessageDTO;
import com.cesar.Message.dto.NewConversationFirstMessageDTO;
import com.cesar.Message.entity.Message;
import com.cesar.Message.repository.MessageRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MessageService {

    public Long storeNewConversationFirstMessage
            (NewConversationFirstMessageDTO message) {

        //Event - create new conversation
        Long newConversationId=null;

        //Event publisher - notify new unread message

        //Store in DB
        Message entity = mapper.map(message, Message.class);
        entity.setConversationId(newConversationId);
        repo.save(entity);
        return newConversationId;
    }

    public Long store(MessageDTO message) {

        //Event publisher - notify new unread message

        //Store in DB
        Message entity = mapper.map(message, Message.class);
        repo.save(entity);
        return entity.getConversationId();
    }

    public List<MessageDTO> getConversationMessages(Long conversationId){

        //Store in Cache with TTL

        return repo.findByConversationId(conversationId)
                .stream()
                .map(m -> mapper.map(m, MessageDTO.class))
                .toList();
    }

    //Event Consumer - Remove messages from Cache
    //When offline event is published

    @Autowired
    private MessageRepository repo;
    @Autowired
    private ModelMapper mapper;
}