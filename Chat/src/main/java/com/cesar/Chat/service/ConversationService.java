package com.cesar.Chat.service;

import com.cesar.Chat.dto.ConversationDTO;
import com.cesar.Chat.dto.MessageForInitDTO;
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

    public void create(ConversationDTO conversation, MessageForInitDTO message){

        //If request is not for recreation
        if(conversation==null){

            //Get message participants IDs
            List<Long> usersIds = Stream
                    .of(
                            message.getSenderId(),
                            message.getRecipientId())
                    .toList();

            //Look for an existent conversation between both users
            ConversationDTO existentConversation = getByParticipantsIds(usersIds);

            //If don't exists
            if(existentConversation==null){

                //----CREATE----

                //Create participants
                List<Participant> participants = participantService.createInBatch(usersIds);

                //Store conversation in DB
                conversation = mapper.map(
                        repo.save(
                                Conversation
                                        .builder()
                                        .createdAt(LocalDateTime.now())
                                        .participants(participants)
                                        .participantsIds(usersIds)
                                        .build()),
                        ConversationDTO.class);
            }
            else{

                //----RECREATE----
                conversation=existentConversation;
            }
        }

        //----COMPOSE DATA----
        injectConversationsDetails(Stream.of(conversation).toList());

        //Set new unread message for each participant (less for sender)
        participantService.increaseUnreadMessages(message.getSenderId(), conversation.getId());

        //----PUBLISH EVENT - ConversationCreated----
        //Data for: conversation and message for WS Service
    }

    public List<ConversationDTO> loadInBatch(List<Long> conversationsIds){
        //Store them in CACHE with TTL
        List<ConversationDTO> conversations =
                repo.findAllById(conversationsIds)
                        .stream()
                        .map(c -> mapper.map(c, ConversationDTO.class))
                        .toList();
        //Set participants presence statuses
        presenceService.injectConversationsParticipantsStatuses(conversations);
        return conversations;
    }

    public void delete(Long participantId, Long conversationId){

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

    public ConversationDTO getById(Long id){
        return mapper.map(repo.getReferenceById(id), ConversationDTO.class);
    }

    public ConversationDTO getByParticipantsIds(List<Long> participantsIds){
        return mapper.map(repo.findByParticipantsIds(participantsIds), ConversationDTO.class);
    }

    public List<Long> getConversationsParticipantsIds(List<ConversationDTO> conversations){
        List<Long> participantsUsersIds = new ArrayList<>();
        conversations
                .forEach(conversation -> {
                    participantsUsersIds.addAll(
                            conversation.getParticipantsIds()
                    );
                });
        return participantsUsersIds;
    }

    public void injectConversationsDetails(List<ConversationDTO> conversations){
        //Set participants user details
        userService.injectConversationsParticipantsDetails(conversations);
        //Set participants presence statuses
        presenceService.injectConversationsParticipantsStatuses(conversations);
    }

    @Autowired
    private ConversationRepository repo;
    @Autowired
    private ParticipantService participantService;
    @Autowired
    private PresenceService presenceService;
    @Autowired
    private UserService userService;
    @Autowired
    private ModelMapper mapper;
}