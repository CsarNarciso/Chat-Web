package com.cesar.ConversationAPI.service;

import com.cesar.ConversationAPI.dto.*;
import com.cesar.ConversationAPI.entity.Conversation;
import com.cesar.ConversationAPI.feign.ParticipantFeign;
import com.cesar.ConversationAPI.repository.ConversationRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
public class ConversationService {

    public Conversation getByUserId(Long userId){
        return repo.findById(userId);
    }

    @Autowired
    private ConversationRepository repo;
    @Autowired
    private ParticipantFeign participantFeign;
    @Autowired
    private ModelMapper mapper;
}