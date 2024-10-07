package com.cesar.Chat.service;

import com.cesar.Chat.dto.CreateConversationRqsDTO;
import com.cesar.Chat.dto.CreateMessageRqsDTO;
import com.cesar.Chat.dto.MessageDTO;
import com.cesar.Chat.dto.SendMessageRqsDTO;
import com.cesar.Chat.entity.Message;
import com.cesar.Chat.repository.MessageRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Service
public class MessageService {

    public MessageDTO save(Message message){
        return mapper.map(repo.save(message), MessageDTO.class);
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