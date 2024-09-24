package com.cesar.Conversation.service;

import com.cesar.Conversation.dto.ConversationDTO;
import com.cesar.Conversation.repository.ConversationRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ConversationService {

    //Event Consumer - Create new conversation
    //when new conversation first message is sent
    //Consume all participants details from image and user services response event
    //Store all in DB

    //Event Consumer - Update unread messages
    //When message is sent/read
    //Update quantity in Cache

    public List<ConversationDTO> loadConversations(List<Long> conversationsId){

        //Store them in Cache, remove after TTL

        return repo.findAllById(conversationsId)
                .stream()
                .map(c -> mapper.map(c, ConversationDTO.class))
                .toList();
    }

    //Event Consumer - Remove conversations in Cache
    //When offline event is published
    //Remove from cache

    //Event Consumer - Store unread messages
    //When offline event is published
    //Store/Update directly in DB
    //Remove from Cache

    @Autowired
    private ConversationRepository repo;
    @Autowired
    private ModelMapper mapper;
}