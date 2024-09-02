package com.cesar.ConversationAPI.service;

import com.cesar.ConversationAPI.dto.ConversationDTO;
import com.cesar.ConversationAPI.entity.Conversation;
import com.cesar.ConversationAPI.repository.ConversationRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ConversationService {

    public ConversationDTO create(Conversation conversation) {

        conversation = repo.save(conversation);
        return mapper.map(conversation, ConversationDTO.class);
    }

    public ConversationDTO getById(Long id) {

        Optional<Conversation> conversation = repo.findById(id);
        return conversation.map(value -> mapper.map(value, ConversationDTO.class)).orElse(null);
    }

    public List<ConversationDTO> getBySenderId(Long senderId) {

        List<Conversation> conversations = repo.findBySenderId(senderId);
        return conversations
                .stream()
                .map(c -> mapper.map(c, ConversationDTO.class))
                .toList();
    }

    public List<ConversationDTO> getByRecipientId(Long recipientId) {

        List<Conversation> conversations = repo.findBySenderId(recipientId);
        return conversations
                .stream()
                .map(c -> mapper.map(c, ConversationDTO.class))
                .toList();
    }

    @Autowired
    private ModelMapper mapper;
    @Autowired
    private ConversationRepository repo;
}
