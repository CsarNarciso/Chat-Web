package com.cesar.Chat.service;

import com.cesar.Chat.dto.ConversationViewDTO;
import com.cesar.Chat.dto.UserDTO;
import com.cesar.Chat.feign.UserFeign;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class UserService {



    public void injectConversationsParticipantsDetails(List<ConversationViewDTO> conversations, List<Long> participantsIds){

        //Fetch details
        Map<Long, UserDTO> details =
                getDetails(participantsIds)
                        .stream()
                        .collect(Collectors.toMap(UserDTO::getId, Function.identity()));
        //If data was fetched
        if(!details.isEmpty()) {
        	
        	//Match details with participants
        	conversations
        		.forEach(c -> {
        			
        			UserDTO recipientDetails = details.get(c.getRecipient().getUserId());
        			if(recipientDetails!=null) {
        				mapper.map(recipientDetails, c.getRecipient());
        			}
        		});
        }
    }

    
    private List<UserDTO> getDetails(List<Long> ids){
        return feign.getDetails(ids);
    }




    public UserService(UserFeign feign, ModelMapper mapper) {
        this.feign = feign;
        this.mapper = mapper;
    }

    private final UserFeign feign;
    private final ModelMapper mapper;
}