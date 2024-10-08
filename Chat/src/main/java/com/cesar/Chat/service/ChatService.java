package com.cesar.Chat.service;

import com.cesar.Chat.dto.ConversationDTO;
import com.cesar.Chat.dto.MessageDTO;
import com.cesar.Chat.dto.MessageForSendDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Stream;

@Service
public class ChatService {

    public void sendMessage(MessageForSendDTO messageRequest) {

        //Get message participants IDs
        List<Long> participantsIds = Stream
                .of(
                        messageRequest.getSenderId(),
                        messageRequest.getRecipientId())
                .toList();

        //Look for an existent conversation between both users
        ConversationDTO conversation = conversationService.loadByParticipantsIds(participantsIds);

        //If no Conversation ID
        if(messageRequest.getConversationId()==null){

            //And a related Conversation doesn't exist
            if(conversation==null){

                //Create
                conversation = conversationService.create(messageRequest);
                messageRequest.setConversationId(conversation.getId());

                //Send message with conversation for both users
                //Publish Event - ConversationCreated
                //when message is sent for a new or recreated conversation
                //Data for: message data for WS Service
            }
        }
        else{

            //Create message data
            MessageDTO message = messageService.send(messageRequest);

            //Send message data only for user who already have conversation
            //Publish Event - MessageSent
            //when message is sent for an existent conversation
            //Data for: message data for WS Service

            //Check if conversation needs to be recreated for someone (recreateFor list of ids)
            //Send message with conversation for users who don't have the conversation
            //Publish Event - ConversationRecreated
            //when message is sent for a new or recreated conversation
            //Data for: message data for WS Service
        }
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