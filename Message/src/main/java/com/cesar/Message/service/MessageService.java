package com.cesar.Message.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Service
public class MessageService {

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

    public MessageDTO save(Message message){
        return mapper.map(repo.save(message), MessageDTO.class);
    }

    public List<MessageDTO> loadConversationMessages(Long conversationId){
        //Get from CACHE, if they are not stored, get from DB and store in CACHE with TTL
        //For the moment, get from DB
        return repo.findByConversationId(conversationId)
                .stream()
                .map(m -> mapper.map(m, MessageDTO.class))
                .toList();
    }

    @Autowired
    private MessageRepository repo;
    @Autowired
    private ConversationService conversationService;
    @Autowired
    private ModelMapper mapper;
}