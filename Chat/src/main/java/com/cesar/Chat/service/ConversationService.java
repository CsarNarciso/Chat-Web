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
        //Set participants' user/presence details and unread messages counts
        injectConversationsDetails(Stream.of(conversation).toList(), message.getSenderId());

        //----PUBLISH EVENT - ConversationCreated----
        //Data for: conversation and message for WS Service
        //Data for: userId, conversationId for Presence Service (Social Graph service if it was implemented)
    }

    public List<ConversationDTO> load(Long participantId){

        List<ConversationDTO> conversations = getByParticipantId(participantId);

        if(!conversations.isEmpty()){

            //Set participants' user/presence details and unread messages counts
            injectConversationsDetails(conversations, participantId);
        }
        return conversations;
    }

    public ConversationDTO cleanUnreadMessages(Long conversationId, Long userId){
        //Update in DB
        messageService.cleanConversationUnreadMessages(conversationId, userId);
        //Clean them in cache (for conversationDTO object)
        return null;
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

        //----PUBLISH EVENT - ConversationDeleted----
        //Data for: userId, conversationId for Presence Service (Social Graph service if it was implemented)

        return mapper.map(conversation, ConversationDTO.class);
    }

    private void injectConversationsDetails(List<ConversationDTO> conversations, Long participantId){
        conversations
                .forEach(conversation -> {

                    List<Long> participantsIds = conversation.getParticipantsIds();
                    List<ParticipantDTO> participants = new ArrayList<>();

                    participantsIds
                            .forEach(id -> {

                                participants.add(ParticipantDTO
                                        .builder()
                                        .userId(id)
                                        .build());
                            });

                    conversation.setParticipants(participants);

                    //Set participants user details
                    userService.injectConversationsParticipantsDetails(
                            conversations,
                            participantsIds);

                    //Set participants presence statuses
                    presenceService.injectConversationsParticipantsStatuses(conversations, participantsIds);

                    //Set new unread message for each participant (less for sender)
                    messageService.injectConversationsUnreadMessages(conversations, participantId);
                });
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

    //----Event Consumer - User Updated---
    //when User services updates a user details or profileImage url
    //Data: userId
    //Task: invalidate that user data (participant) in cache

    //----Event Consumer - User Deleted---
    //when User services deletes a user
    //Data: userId
    //Task: invalidate user data in cache and delete all user messages

    @Autowired
    private ConversationRepository repo;
    @Autowired
    private PresenceService presenceService;
    @Autowired
    private MessageService messageService;
    @Autowired
    private UserService userService;
    @Autowired
    private ModelMapper mapper;
}