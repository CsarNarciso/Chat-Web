package com.cesar.Social.service;

import com.cesar.Social.dto.ConversationCreatedDTO;
import com.cesar.Social.dto.ConversationDeletedDTO;
import com.cesar.Social.dto.RelationshipDTO;
import com.cesar.Social.entity.Relationship;
import com.cesar.Social.repository.RelationshipRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class RelationshipService {

    public RelationshipDTO getByUserId(Long userId){
        return mapper.map(repo.findByUserId(userId), RelationshipDTO.class);
    }

    //Event Consumer - ConversationCreated
    //action(create, recreate), participantsIds, recreatedForIds, conversationId
    public void onConversationCreated(ConversationCreatedDTO conversation){

        List<Relationship> relationships = new ArrayList<>();

        //Check for action to perform
        switch (conversation.getAction()){
            case "CREATED":
                relationships = repo.findByUsersIds(conversation.getParticipantsIds());
            case "RECREATED":
                relationships = repo.findByUsersIds(conversation.getRecreatedForIds());
        }

        //Add conversationId to user relationships
        relationships
                .forEach(relationship -> {
                    relationship.getConversationsIds().add(conversation.getConversationId());
                });
        repo.saveAll(relationships);
    }

    //Event Consumer - ConversationDeleted
    //participantId, conversationId
    public void onConversationDeleted(ConversationDeletedDTO conversation){

        Relationship relationship = repo.findByUserId(conversation.getParticipantId());

        //Remove conversationId from user relationships
        relationship.getConversationsIds().removeIf(c -> c.equals(conversation.getConversationId()));
        repo.save(relationship);
    }

    @Autowired
    private RelationshipRepository repo;
    @Autowired
    private ModelMapper mapper;
}