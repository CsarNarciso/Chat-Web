package com.cesar.Message.service;

import com.cesar.Message.dto.MessageDTO;
import com.cesar.Message.entity.Message;
import com.cesar.Message.repository.MessageRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MessageService {

    //NOTE: save in db after new message created kafka event is published from messageAPI

    public void create(Message message) {
        repo.save(message);
    }

    public List<MessageDTO> getConversationMessages(Long conversationId){
        List<Message> messages = repo.findByConversationId(conversationId);
        return messages
                .stream()
                .map(m -> mapper.map(m, MessageDTO.class))
                .toList();
    }

    @Autowired
    private MessageRepository repo;
    @Autowired
    private ModelMapper mapper;
}