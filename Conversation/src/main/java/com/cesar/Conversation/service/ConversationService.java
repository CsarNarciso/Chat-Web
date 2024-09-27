package com.cesar.Conversation.service;

import com.cesar.Conversation.dto.ConversationDTO;
import com.cesar.Conversation.dto.FirstMessageDTO;
import com.cesar.Conversation.dto.MessageDTO;
import com.cesar.Conversation.entity.Conversation;
import com.cesar.Conversation.entity.Participant;
import com.cesar.Conversation.repository.ConversationRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Service
public class ConversationService {

    public void create(FirstMessageDTO firstMessage){

        Long senderId = firstMessage.getSenderId();
        Long recipientId = firstMessage.getRecipientId();
        List<Long> participantsIds = Stream.of(senderId, recipientId).toList();

        //Fetch participants details
        List<Participant> participants = userService.getParticipantsDetails(participantsIds);

        //Set initial participants unread messages
        participantService.setInitialConversationUnreadMessages(senderId, participants);

        //Store conversation in DB
        ConversationDTO conversation = mapper.map(
                repo.save(
                    Conversation
                            .builder()
                            .createdAt(LocalDateTime.now())
                            .participants(participants)
                            //Set conversation last message data
                            .lastMessageContent(firstMessage.getContent())
                            .lastMessageSentAt(firstMessage.getSentAt())
                            .build()),
                ConversationDTO.class);

        //Set participants presence statuses
        presenceService.injectConversationsParticipantsStatuses(
                Stream.of(conversation).toList());

        //Event Publisher - New conversation created
        //when new conversation and its participants are created
        //Data for: conversationId for user conversationsIds attribute
        //Data for: FirstMessageDTO with conversationId for message service
        //Data for: ConversationDTO for WS server
    }

    public List<ConversationDTO> loadConversations(List<Long> conversationsId){
        List<ConversationDTO> conversations =
                repo.findAllById(conversationsId)
                        .stream()
                        .map(c -> mapper.map(c, ConversationDTO.class))
                        .toList();
        presenceService.injectConversationsParticipantsStatuses(conversations);
        return conversations;
    }

    //Event Consumer - User data/profile update
    //When user updates its data/profile
    //Update conversation participants data. Update it in Cache and directly in DB

    //Event Consumer - New message
    //When existent conversation message is sent
    //Task: Update unread messages (increase) and last message. Update it in Cache,
    // after certain amount, update directly in DB
    //Data: MessageDTO{conversationId, content, sentAt}
    public void updateByNewMessage(MessageDTO message){
        //Update in CACHE, update in DB after user offline event
        //For the moment, update directly in DB
        //Update last message data
        repo.save(Conversation
                .builder()
                .id(message.getConversationId())
                .lastMessageContent(message.getContent())
                .lastMessageSentAt(message.getSentAt())
                .build());
        //Increase unread messages for recipient
        participantService.increaseUnreadMessages(message.getRecipientId());
    }

    public void cleanUnreadMessages(Long userId){
        //Update in DB
        participantService.cleanUnreadMessages(userId);
        //Update in CACHE
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