package com.cesar.Conversation.service;

import com.cesar.Conversation.dto.ConversationDTO;
import com.cesar.Conversation.dto.ParticipantDTO;
import com.cesar.Conversation.dto.PresenceStatusDTO;
import com.cesar.Conversation.entity.Conversation;
import com.cesar.Conversation.entity.Participant;
import com.cesar.Conversation.feign.PresenceFeign;
import com.cesar.Conversation.feign.UserFeign;
import com.cesar.Conversation.repository.ConversationRepository;
import com.cesar.Conversation.repository.ParticipantRepository;
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
        Long senderId = event.get("senderId");
        List<Long> participantsIds = Stream.of(
                senderId,
                event.get("recipientId"));

        //Store conversation in DB
        Conversation conversation = conversationRepo.save(
                Conversation
                        .builder()
                        .createdAt(LocalDateTime.now())
                        .build());

        //Fetch participants details
        List<Participant> participants = userFeign.getDetails(participantsIds)
                .stream()
                .map(d -> mapper.map(d, Participant.class))
                .toList();
        participants
                .forEach(participant -> {
                    participant = Participant
                            .builder()
                            .conversationId(conversation.getId())
                            .unreadMessages(
                                    //Sender doesn't increment unread message
                                    participant.getUserId().equals(senderId)
                                            ? 0 : 1)
                            .build();
                });
        //Store participants in DB
        participantRepo.saveAll(participants);

        //Event Publisher - Save conversation reference in User
        //when new conversation is created
        //Event data: conversationId for user conversationsIds attribute

        //Fetch participants presence statuses
        List<PresenceStatusDTO> statuses = presenceFeign.getStatuses(participantsIds);

        //Compose data
        ConversationDTO conversationDTO = mapper.map(conversation, ConversationDTO.class);
        List<ParticipantDTO> participantsDTOs = participants
                    .stream()
                    .map(p -> mapper.map(p, ParticipantDTO.class))
                    .toList();
        conversationDTO.setParticipantsDetails(participantsDTOs);

        //Sent new Conversation data
        simp.convertAndSend("newConversationDestination", conversationDTO);
    }

    public List<ConversationDTO> loadConversations(List<Long> conversationsId){

        //Get participants
        List<Participant> participants =
                participantRepo.findAllByConversationId(conversationsId);

        //Fetch presence presence statuses
        List<Long> participantsIds = participants
                .stream()
                .map(Participant::getUserId)
                .toList();

        List<PresenceStatusDTO> statuses =
                presenceFeign.getStatuses(participantsIds);

        //Match presence statuses with participants
        List<ParticipantDTO> participantsDTOS = participants
                .stream()
                .map(p -> mapper.map(p, ParticipantDTO.class))
                .toList();

        participantsDTOS
                .forEach(participant -> {
                    mapper.map(participant, statuses.get(participant.getUserId().intValue()));
                });

        //Get conversations
        List<Conversation> conversations =
                conversationRepo.findAllById(conversationsId);

        List<ConversationDTO> conversationDTOS = conversations
                .stream()
                .map(c -> mapper.map(c, ConversationDTO.class))
                .toList();

        //Compose Data
        conversationDTOS
                .forEach(conversation -> {
                    //Find all current conversation participants
                    List<ParticipantDTO> currentConversationParticipants = participantsDTOS
                            .stream()
                            .takeWhile(p -> p.getConversationId().equals(conversation.getId()))
                            .toList();
                    conversation.setParticipantsDetails(currentConversationParticipants);
                });
        return conversationDTOS;
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
    private ParticipantRepository participantRepo;
    @Autowired
    private PresenceFeign presenceFeign;
    @Autowired
    private UserFeign userFeign;
    @Autowired
    private SimpMessagingTemplate simp;
    @Autowired
    private ModelMapper mapper;
}