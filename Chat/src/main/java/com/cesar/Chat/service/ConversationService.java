package com.cesar.Chat.service;

import com.cesar.Chat.dto.*;
import com.cesar.Chat.entity.Conversation;
import com.cesar.Chat.repository.ConversationRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
public class ConversationService {

    public void create(ConversationDTO conversation, MessageForInitDTO message){

        List<Long> createFor = new ArrayList<>();

        //If request is not for recreation
        if(conversation==null){

            //Get message participants IDs
            List<Long> usersIds = Stream
                    .of(
                            message.getSenderId(),
                            message.getRecipientId())
                    .toList();

            //Look for an existent conversation between both users
            Conversation existentConversation = getByParticipantsIds(usersIds);

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
                createFor = conversation.getParticipantsIds();
            }
            else{

                //----RECREATE----
                repo.save(
                        Conversation
                                .builder()
                                .recreateFor(null)
                                .build());
                createFor = existentConversation.getRecreateFor();
                conversation=mapper.map(existentConversation, ConversationDTO.class);
            }
        }

        //----COMPOSE DATA----
        //Set participants' user/presence details and unread messages counts
        injectConversationsDetails(Stream.of(conversation).toList(), message.getSenderId());

        //----PUBLISH EVENT - ConversationCreated----
        kafkaTemplate.send("ConversationCreated", ConversationCreatedDTO
                .builder()
                .conversationId(conversation.getId())
                .createFor(createFor)
                .build());


        //----SEND CONVERSATION DATA----
        for (Long participantId : createFor) {
            messagingTemplate.convertAndSendToUser(participantId.toString(), "/user/reply", conversation);
        }
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
        kafkaTemplate.send("ConversationDeleted", ConversationDeletedDTO
                .builder()
                .conversationId(conversationId)
                .participantId(participantId)
                .build());

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

    private Conversation getByParticipantsIds(List<Long> participantsIds){
        return repo.findByParticipantsIds(participantsIds);
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
    //when User service updates a user details
    //Data: userId
    //Task: invalidate that user data (participant) in cache
    @KafkaListener
    public void onUserUpdate(){

    }

    //----Event Consumer - User Image Updated---
    //when User service updates a user profileImage
    //Data: userId, new imageUrl
    //Task: invalidate that user data (participant) in cache
    @KafkaListener
    public void onUserImageUpdate(){

    }

    //----Event Consumer - User Deleted---
    //when User services deletes a user
    //Data: userId
    //Task: invalidate user data in cache and delete all user messages
    @KafkaListener
    public void onUserDelete(){

    }

    @Autowired
    private ConversationRepository repo;
    @Autowired
    private PresenceService presenceService;
    @Autowired
    private MessageService messageService;
    @Autowired
    private UserService userService;
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private ModelMapper mapper;
}