package com.cesar.MessageAggregator.service;

import com.cesar.MessageAggregator.dto.ComposedMessageDTO;
import com.cesar.MessageAggregator.entity.ComposedMessage;
import com.cesar.MessageAggregator.repository.ComposedMessageRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ComposedMessageService {

    //NOTE: save in db after new message created kafka event is published from messageAPI

    public List<ComposedMessageDTO> getConversationMessages(Long conversationId){
        List<ComposedMessage> messages = repo.findByConversationId(conversationId);
        return messages
                .stream()
                .map(m -> mapper.map(m, ComposedMessageDTO.class))
                .toList();
    }

    @Autowired
    private ComposedMessageRepository repo;
    @Autowired
    private ModelMapper mapper;
}