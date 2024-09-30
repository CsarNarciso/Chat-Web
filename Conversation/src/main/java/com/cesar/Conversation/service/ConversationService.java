package com.cesar.Conversation.service;

import com.cesar.Conversation.dto.ConversationDTO;
import com.cesar.Conversation.dto.CreationRequestDTO;
import com.cesar.Conversation.dto.CreationResponseDTO;
import com.cesar.Conversation.dto.MessageDTO;
import com.cesar.Conversation.entity.Conversation;
import com.cesar.Conversation.entity.Participant;
import com.cesar.Conversation.repository.ConversationRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
public class ConversationService {

    public CreationResponseDTO create(CreationRequestDTO creationRequest){

        List<Long> participantsIds = creationRequest.getParticipantsIds();

        //Fetch participants details
        List<Participant> participants = userService.getParticipantsDetails(participantsIds);

        //Store conversation in DB
        ConversationDTO conversation = mapper.map(
                repo.save(
                    Conversation
                            .builder()
                            .createdAt(LocalDateTime.now())
                            .participants(participants)
                            .build()),
                ConversationDTO.class);

        //Set participants presence statuses
        presenceService.injectConversationsParticipantsStatuses(
                Stream.of(conversation).toList());

        //Set new unread message for each participant (less for sender)
        participantService.increaseUnreadMessages(creationRequest.getSenderId(), conversation.getId());

        //Event Publisher - New conversation created
        //when new conversation and its participants are created
        //Data for: conversationId for user conversationsIds attribute
        //Data for: ConversationDTO for WS server

        return CreationResponseDTO
                .builder()
                .id(conversation.getId())
                .participantsUsersIds(participantsIds)
                .recreateForSomeone(false)
                .build();
    }

    public CreationResponseDTO recreate(Long conversationId){

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

        return CreationResponseDTO
                .builder()
                .id(conversationId)
                .participantsUsersIds(participantsIds)
                .recreateForSomeone(false)
                .build();
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

    public void deleteConversation(Long userId, Long conversationId){
        //Verify if deletion is permanently or just by a participant:
        //this by adding userId to conversation recreateFor list, if after that
        //recreateFor list matches conversation participants ids, deletion is permanently
        //Update in DB and CACHE with TTL
        //Event Publisher - Conversation Deleted
        //when conversation is deleted by user or permanently
        //Data for: userId, conversationId for User, Message and WS service

    }

    //Event Consumer - User data/profile update
    //When user updates its data/profile
    //Update conversation participants data. Update it in Cache and directly in DB (because it's not common this happens)

    //Event Consumer - New message
    //When existent conversation message is sent
    //Data: MessageDTO{conversationId, content, sentAt}
    public void updateByNewMessage(MessageDTO message){
        //Update in CACHE, after certain time, update in DB
        //For the moment, update directly in DB
        //Update last message data
        repo.save(
                Conversation
                        .builder()
                        .id(message.getConversationId())
                        .lastMessageContent(message.getContent())
                        .lastMessageSentAt(message.getSentAt())
                        .build());
        //Increase unread messages for recipient
        participantService.increaseUnreadMessages(message.getSenderId(), message.getConversationId());
    }

    public void closeConversation(Long participantId){
        //Clean Unread Messages
        //In CACHE first, after certain time, in DB
        //For the moment in DB
        participantService.cleanUnreadMessages(participantId);
    }

    @Autowired
    private ConversationRepository repo;
    @Autowired
    private UserService userService;
    @Autowired
    private ParticipantService participantService;
    @Autowired
    private PresenceService presenceService;
    @Autowired
    private ModelMapper mapper;
}