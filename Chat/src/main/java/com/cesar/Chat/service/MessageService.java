package com.cesar.Chat.service;

import com.cesar.Chat.dto.*;
import com.cesar.Chat.entity.Conversation;
import com.cesar.Chat.entity.Message;
import com.cesar.Chat.repository.MessageRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MessageService {

    public void send(MessageForSendDTO message){

        Conversation conversation = conversationService.getById(message.getConversationId());

        //If conversation exists,
        if(conversation!=null){

            //and user belongs to
            if(conversation.getParticipantsIds().contains(message.getSenderId())){

                Message entity = mapper.map(message, Message.class);
                entity.setRead(false);
                entity.setSentAt(LocalDateTime.now());

                //Add new unread message
                //Add in CACHE to correspondent ConversationDTO object

                //Save Message
                repo.save(entity);

                //Send
                messagingTemplate.convertAndSend(
                        "/topic/conversation/"+conversation.getId(),
                        message);

                //If conversation needs to be recreated for someone
                if(!conversation.getRecreateFor().isEmpty()){
                    conversationService.create(
                            mapper.map(conversation, ConversationDTO.class),
                            mapper.map(message, MessageForInitDTO.class));
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

    public void cleanConversationUnreadMessages(Long conversationId, Long senderId){
        repo.cleanConversationUnreadMessages(conversationId, senderId);
    }

    public void injectConversationsUnreadMessages(List<ConversationDTO> conversations, Long senderId){

        //Fetch unreadMessages
        Map<Long, Integer> unreadMessages =
                repo.getUnreadMessages(senderId)
                        .stream()
                        .collect(Collectors.toMap(
                                UnreadMessagesDTO::getConversationId,
                                UnreadMessagesDTO::getCount));
        //Match unreadMessages with conversations
        conversations
                .forEach(conversation -> {
                    conversation.setUnreadMessages(unreadMessages.get(conversation.getId()));
                });
    }

    @Autowired
    private MessageRepository repo;
    @Autowired
    private ConversationService conversationService;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private ModelMapper mapper;
}