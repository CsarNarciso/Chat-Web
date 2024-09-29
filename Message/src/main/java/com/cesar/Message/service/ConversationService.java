package com.cesar.Message.service;

import com.cesar.Message.feign.ConversationFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ConversationService {

    public Long create(List<Long> participantsIds){
        return conversationFeign.create(participantsIds);
    }
    public void recreate(Long conversationId, List<Long> recreateFor){
        conversationFeign.recreate(conversationId, recreateFor);
    }
    @Autowired
    private ConversationFeign conversationFeign;
}