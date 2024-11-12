package com.cesar.Chat.service;

import com.cesar.Chat.dto.ConversationViewDTO;
import com.cesar.Chat.dto.UserPresenceDTO;
import com.cesar.Chat.feign.PresenceFeign;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PresenceService {


    public void injectConversationsParticipantsStatuses(List<ConversationViewDTO> conversations,
                                                        List<Long> participantsIds){
        //Fetch presence statuses
        Map<Long, UserPresenceDTO> statuses =
                feign.getByUserIds(participantsIds)
                        .stream()
                        .collect(Collectors.toMap(UserPresenceDTO::getId, Function.identity()));

        //If data was fetched
        if(!statuses.isEmpty()) {
        
        	//Match statuses with participants
        	conversations
                	.forEach(c -> {
                		
                		UserPresenceDTO recipientStatus = statuses.get(c.getRecipient().getUserId());
                		
                		if(recipientStatus!=null) {
                			mapper.map(c.getRecipient(), recipientStatus);
                		}
                	});
        }
    }




    public PresenceService(PresenceFeign feign, ModelMapper mapper) {
        this.feign = feign;
        this.mapper = mapper;
    }

    private final PresenceFeign feign;
    private final ModelMapper mapper;
}