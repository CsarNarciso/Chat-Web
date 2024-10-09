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
        //Set participants user/presence details
        injectConversationsDetails(Stream.of(conversation).toList());
        //Set new unread message for each participant (less for sender)
        participantService.increaseUnreadMessages(message.getSenderId(), conversation.getId());

        //----PUBLISH EVENT - ConversationCreated----
        //Data for: conversation and message for WS Service
    }

    public List<ConversationDTO> load(Long participantId){

        List<ConversationDTO> conversations = getByParticipantId(participantId);

        if(!conversations.isEmpty()){

            //Set participants user/presence details
            injectConversationsDetails(conversations);
        }
        return conversations;
    }

    public ConversationDTO delete(Long conversationId, Long participantId){

        //Look for conversation
        ConversationDTO conversation = getById(conversationId);
        boolean permanently;

        //If exists...
        if(conversation!=null){

            //Get conversation participants Ids
            List<Long> participantsIds = conversation.getParticipantsIds();

            //Add userId to conversation recreateFor list
            List<Long> recreateFor = conversation.getRecreateFor();
            recreateFor.add(participantId);

            //If recreateFor matches participantsIds...
            if(participantsIds.equals(recreateFor)){

                //Deletion is permanently
                permanently = true;
                repo.deleteById(conversationId);
            }
            //If not,
            else{

                //Update recreateFor list
                permanently = false;
                repo.save(
                        Conversation
                                .builder()
                                .id(conversationId)
                                .recreateFor(recreateFor)
                                .build()
                );
            }
        }
        return conversation;
    }

    private ConversationDTO getById(Long id){
        return mapper.map(repo.getReferenceById(id), ConversationDTO.class);
    }

    private List<ConversationDTO> getByParticipantId(Long participantId){
        return repo.findByParticipantId(participantId)
                .stream()
                .map(c -> mapper.map(c, ConversationDTO.class))
                .toList();
    }

    private ConversationDTO getByParticipantsIds(List<Long> participantsIds){
        return mapper.map(repo.findByParticipantsIds(participantsIds), ConversationDTO.class);
    }

    private List<Long> getConversationsParticipantsIds(List<ConversationDTO> conversations){
        List<Long> participantsUsersIds = new ArrayList<>();
        conversations
                .forEach(conversation -> {
                    participantsUsersIds.addAll(
                            conversation.getParticipantsIds()
                    );
                });
        return participantsUsersIds;
    }

    private void injectConversationsDetails(List<ConversationDTO> conversations){
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