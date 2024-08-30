package com.cesar.MessageAPI.service;

import com.cesar.MessageAPI.dto.MessageDTO;
import com.cesar.MessageAPI.entity.Message;
import com.cesar.MessageAPI.repository.MessageRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MessageService {

    public MessageDTO create(Message message){

        message = repo.save(message);
        return mapper.map(message, MessageDTO.class);
    }

    public MessageDTO getById(Long id) {

        Optional<Message> message = repo.findById(id);

        if(message.isPresent()){

            return mapper.map(message, MessageDTO.class);
        }
        return null;
    }

    public List<MessageDTO> getBySenderId(Long senderId) {

        List<Message> messages = repo.findBySenderId(senderId);

        if(!messages.isEmpty()){

            return messages
                    .stream()
                    .map(m -> mapper.map(m, MessageDTO.class))
                    .toList();
        }
        return null;
    }

    public List<MessageDTO> getByRecipientId(Long recipientId) {

        List<Message> messages = repo.findByRecipientId(recipientId);

        if(!messages.isEmpty()){

            return messages
                    .stream()
                    .map(m -> mapper.map(m, MessageDTO.class))
                    .toList();
        }
        return null;
    }

    @Autowired
    private ModelMapper mapper;
    @Autowired
    private MessageRepository repo;
}