package com.cesar.Chat.service;

import com.cesar.Chat.dto.ConversationDTO;
import com.cesar.Chat.dto.MessageDTO;
import com.cesar.Chat.dto.MessageForInitDTO;
import com.cesar.Chat.dto.MessageForSendDTO;
import com.cesar.Chat.entity.Message;
import com.cesar.Chat.repository.MessageRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageService {

    public void send(MessageForSendDTO message){

        ConversationDTO conversation = conversationService.getById(message.getConversationId());

        //If conversation exists,
        if(conversation!=null){

            //and user belongs to
            if(conversation.getParticipantsIds().contains(message.getSenderId())){

                //Save Message
                Message entity = mapper.map(message, Message.class);
                entity.setSentAt(LocalDateTime.now());
                repo.save(entity);

                //Publish Event - MessageSent
                //Data for: message for WS Service

                //If conversation needs to be recreated for someone
                if(!conversation.getRecreateFor().isEmpty()){
                    conversationService.create(conversation, mapper.map(message, MessageForInitDTO.class));
                }
            }
        }
    }

    public List<MessageDTO> loadConversationMessages(Long conversationId){
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