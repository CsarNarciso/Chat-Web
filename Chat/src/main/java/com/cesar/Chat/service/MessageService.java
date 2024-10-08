package com.cesar.Chat.service;

import com.cesar.Chat.dto.MessageDTO;
import com.cesar.Chat.dto.MessageForSendDTO;
import com.cesar.Chat.entity.Message;
import com.cesar.Chat.repository.MessageRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageService {

    public MessageDTO send(MessageForSendDTO sendRequest){

        //Store in DB
        Message entity = mapper.map(sendRequest, Message.class);
        entity.setSentAt(LocalDateTime.now());
        return mapper.map(repo.save(entity), MessageDTO.class);
    }

    public List<MessageDTO> loadConversationMessages(Long conversationId){
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