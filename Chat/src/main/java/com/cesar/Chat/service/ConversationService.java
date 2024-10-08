package com.cesar.Chat.service;

import com.cesar.Chat.dto.ConversationDTO;
import com.cesar.Chat.dto.MessageForSendDTO;
import com.cesar.Chat.entity.Conversation;
import com.cesar.Chat.entity.Participant;
import com.cesar.Chat.repository.ConversationRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
public class ConversationService {

    public ConversationDTO create(MessageForSendDTO createRequest){

        List<Long> participantsIds = Stream
                .of(createRequest.getSenderId(), createRequest.getRecipientId())
                .toList();

        //Fetch participants details
        List<Participant> participants = participantService.getParticipantsDetails(participantsIds);

        //Store conversation in DB
        ConversationDTO conversation = mapper.map(
                repo.save(
                        Conversation
                                .builder()
                                .createdAt(LocalDateTime.now())
                                .participants(participants)
                                .participantsIds(participantsIds)
                                .build()),
                ConversationDTO.class);

        //Set participants presence statuses
        presenceService.injectConversationsParticipantsStatuses(
                Stream.of(conversation).toList());

        //Set new unread message for each participant (less for sender)
        participantService.increaseUnreadMessages(createRequest.getSenderId(), conversation.getId());

        return conversation;
    }

    public ConversationDTO recreate(Long conversationId){

        //Load conversation data from CACHE
        //If it is not in, get from DB and store in CACHE with TTL
        Conversation entity = repo.getReferenceById(conversationId);

        //Get participants/user ids from conversation recreateFor user ids
        List<Long> participantsIds = entity.getParticipants()
                .stream()
                .map((Participant::getId))
                .toList();
        //Update (empty) recreateFor list attribute in CACHE and DB
        entity = repo.save(
                Conversation
                        .builder()
                        .id(conversationId)
                        .recreateFor(new ArrayList<>())
                        .build()
        );
        //Update unread messages to 1 for each participant in recreateFor list
        participantService.setUnreadMessagesInOne(participantsIds);

        //Compose Conversation Data
        ConversationDTO conversation = mapper.map(entity, ConversationDTO.class);
        //Set participants presence statuses
        presenceService.injectConversationsParticipantsStatuses(Stream.of(conversation).toList());
        //Update conversation in CACHE with TTL

        //Event Publisher - Recreate Conversation
        //when conversation needs to be recreated for participants in recreateFor list attribute
        //Data for: ConversationDTO for WS Server and User service

        return ConversationDTO
                .builder()
                .id(conversationId)
                .build();
    }

    public ConversationDTO loadByParticipantsIds(List<Long> participantsIds){
        return mapper.map(repo.findByParticipantsIds(participantsIds), ConversationDTO.class);
    }

    public List<ConversationDTO> loadConversations(List<Long> conversationsId){
        //Store them in CACHE with TTL
        List<ConversationDTO> conversations =
                repo.findAllById(conversationsId)
                        .stream()
                        .map(c -> mapper.map(c, ConversationDTO.class))
                        .toList();
        //Set participants presence statuses
        presenceService.injectConversationsParticipantsStatuses(conversations);
        return conversations;
    }

    public void deleteConversation(Long participantId, Long conversationId){

        Conversation conversation = repo.getReferenceById(conversationId);
        boolean permanently;
        //Get conversation participants Ids
        List<Long> participantsIds = conversation.getParticipants()
                .stream()
                .map(Participant::getId)
                .toList();
        //Add userId to conversation recreateFor list
        List<Long> recreateFor = conversation.getRecreateFor();
        recreateFor.add(participantId);
        //If recreateFor Ids matches participants Ids...
        if(participantsIds.equals(recreateFor)){
            //Deletion is permanently
            permanently = true;
            repo.delete(conversation);
        }
        //If not,
        else{
            //Update in DB and CACHE with TTL
            permanently = false;
            repo.save(
                    Conversation
                            .builder()
                            .id(conversationId)
                            .recreateFor(recreateFor)
                            .build()
            );
        }
        //Event Publisher - Conversation Deleted
        //when conversation is deleted by user or permanently
        //Data for: userId, conversationId for User, and WS service
        //Data for: conversationId, recreateFor, isPermanently for Message
    }

    //New message

    //Clean Unread Messages

    @Autowired
    private ConversationRepository repo;
    @Autowired
    private ParticipantService participantService;
    @Autowired
    private PresenceService presenceService;
    @Autowired
    private ModelMapper mapper;
}