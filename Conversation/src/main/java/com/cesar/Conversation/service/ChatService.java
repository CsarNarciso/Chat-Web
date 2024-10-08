package com.cesar.Conversation.service;

import com.cesar.Conversation.dto.ConversationDTO;
import com.cesar.Conversation.dto.CreateConversationRqsDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Service
public class ChatService {

    public void send(SendMessageRqsDTO message) {

        List<Long> participantsIds = Stream.of(message.getSenderId(), message.getRecipientId()).toList();

        //If message has no conversation ID
        //(either conversation doesn't exist yet, or sender has deleted it for itself)
        if(message.getConversationId()==null){

            //Verify if conversation between both users exists:
            //by checking if a conversation has those users ids in participants attribute
            ConversationDTO foundConversation = conversationService.getByParticipantsIds(participantsIds);

            //If not...
            if(foundConversation == null){
                //Create new one and get new conversation id
                Long newConversationId = conversationService.create(CreateConversationRqsDTO
                        .builder()
                        .senderId(message.getSenderId())
                        .participantsIds(participantsIds)
                        .build()).getId();
            }
            //If yes...
            else{
                //Get conversation id
                //and recreate for sender (along with other participants in recreateFor attribute) in next step
                message.setConversationId(foundConversation.getId());
            }

        }

        //Check if recreate for someone:
        //If yes...
        //Recreate conversation for those participants
        conversationService.recreate(message.getConversationId(), List.of(message.getRecipientId()));

        //Store in DB
        Message entity = mapper.map(message, Message.class);
        entity.setSentAt(LocalDateTime.now());
        messageService.save(entity);

        //Event Publisher - New message
        //when new message is stored in DB/CACHE
        //Data for: message data (DTO) for WS Service
    }

    @Autowired
    private ConversationService conversationService;
    @Autowired
    private MessageService messageService;
    @Autowired
    private ParticipantService participantService;
    @Autowired
    private ModelMapper mapper;
}