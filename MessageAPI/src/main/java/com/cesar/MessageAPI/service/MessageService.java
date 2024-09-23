package com.cesar.MessageAPI.service;

import com.cesar.MessageAPI.dto.MessageDTO;
import com.cesar.MessageAPI.entity.Message;
import com.cesar.MessageAPI.repository.MessageRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MessageService {

    public void create(Message message) {
        repo.save(message);
    }

    public List<MessageDTO> getConversationMessages(Long conversationId) {
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