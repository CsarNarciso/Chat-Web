package com.cesar.Conversation.service;

import com.cesar.Conversation.dto.ConversationDTO;
import com.cesar.Conversation.entity.Conversation;
import com.cesar.Conversation.entity.Participant;
import com.cesar.Conversation.repository.ConversationRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Service
public class ConversationService {

    //Event Consumer - Create new OneOnOne conversation
    //when new conversation first message is sent
    //Event data: senderId, recipientId
    public void newConversationEvent(){

        //Get event data
        Long senderId = 1L;//event.get("senderId");
        List<Long> participantsIds = Stream.of(senderId, 2L).toList(); //event.get("recipientId"));

        //Fetch participants details
        List<Participant> participants = userService.getParticipantsDetails(participantsIds);

        //Set initial participants unread messages
        participantService.incrementNewUnreadMessage(senderId, participants);

        //Store conversation in DB
        ConversationDTO conversationDTO = mapper.map(
                conversationRepo.save(
                    Conversation
                        .builder()
                        .createdAt(LocalDateTime.now())
                        .participants(participants)
                        .build()),
                ConversationDTO.class);

        //Set participants presence statuses
        presenceService.injectConversationsParticipantsStatuses(
                Stream.of(conversationDTO).toList());

        //Event Publisher - Save conversation reference in User
        //when new conversation and its participants are created
        //Event data: conversationId for user conversationsIds attribute

        //Send new Conversation data
        simp.convertAndSend("newConversationDestination", conversationDTO);
    }

    public List<ConversationDTO> loadConversations(List<Long> conversationsId){

        List<ConversationDTO> conversations =
                conversationRepo.findAllById(conversationsId)
                        .stream()
                        .map(c -> mapper.map(c, ConversationDTO.class))
                        .toList();
        presenceService.injectConversationsParticipantsStatuses(conversations);
        return conversations;
    }

    //Event Consumer - Update unread messages
    //When message is sent/read
    //Update quantity in Cache

    //Event Consumer - Remove conversations in Cache
    //When offline event is published
    //Remove from cache

    //Event Consumer - Store unread messages
    //When offline event is published
    //Store/Update directly in DB
    //Remove from Cache

    @Autowired
    private ConversationRepository conversationRepo;
    @Autowired
    private UserService userService;
    @Autowired
    private ParticipantService participantService;
    @Autowired
    private PresenceService presenceService;
    @Autowired
    private SimpMessagingTemplate simp;
    @Autowired
    private ModelMapper mapper;
}