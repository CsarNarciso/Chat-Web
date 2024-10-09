package com.cesar.Chat.service;

import com.cesar.Chat.dto.ConversationDTO;
import com.cesar.Chat.dto.MessageForInitDTO;
import com.cesar.Chat.dto.ParticipantDTO;
import com.cesar.Chat.entity.Conversation;
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
                conversation = mapper.map(
                        repo.save(
                                Conversation
                                        .builder()
                                        .createdAt(LocalDateTime.now())
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
        createParticipants(
                Stream.of(conversation).toList());
        //Set new unread message for each participant (less for sender)

        //----PUBLISH EVENT - ConversationCreated----
        //Data for: conversation and message for WS Service
    }

    public List<ConversationDTO> load(Long participantId){

        List<ConversationDTO> conversations = getByParticipantId(participantId);

        if(!conversations.isEmpty()){

            //Set participants user/presence details
            createParticipants(conversations);
        }
        return conversations;
    }

    public ConversationDTO delete(Long conversationId, Long participantId){

        //Look for conversation
        Conversation conversation = getById(conversationId);
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
        return mapper.map(conversation, ConversationDTO.class);
    }

    private void createParticipants(List<ConversationDTO> conversations){
        conversations
                .forEach(conversation -> {

                    List<Long> participantsIds = conversation.getParticipantsIds();
                    List<ParticipantDTO> participants = new ArrayList<>();

                    participantsIds
                            .forEach(participantId -> {

                                participants.add(ParticipantDTO
                                        .builder()
                                        .userId(participantId)
                                        .build());
                            });

                    conversation.setParticipants(participants);

                    //Set participants user details
                    userService.injectConversationsParticipantsDetails(
                            conversations,
                            participantsIds);

                    //Set participants presence statuses
                    presenceService.injectConversationsParticipantsStatuses(conversations, participantsIds);
                });
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

    private ConversationDTO getByParticipantsIds(List<Long> participantsIds){
        return mapper.map(repo.findByParticipantsIds(participantsIds), ConversationDTO.class);
    }

    private List<ConversationDTO> getByParticipantId(Long participantId){
        return repo.findByParticipantId(participantId)
                .stream()
                .map(c -> mapper.map(c, ConversationDTO.class))
                .toList();
    }

    public Conversation getById(Long id){
        return repo.getReferenceById(id);
    }

    @Autowired
    private ConversationRepository repo;
    @Autowired
    private PresenceService presenceService;
    @Autowired
    private UserService userService;
    @Autowired
    private ModelMapper mapper;
}