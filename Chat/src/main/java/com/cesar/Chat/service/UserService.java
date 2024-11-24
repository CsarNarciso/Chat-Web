package com.cesar.Chat.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.cesar.Chat.dto.ConversationViewDTO;
import com.cesar.Chat.dto.UserDTO;
import com.cesar.Chat.feign.UserFeign;

@Service
public class UserService {



    public void injectConversationsParticipantsDetails(List<ConversationViewDTO> conversations, List<Long> participantsIds, List<Long> deletedParticipantsIds){

		Map<Long, UserDTO> details = new HashMap<>();

		//Fetch details from user Service
		if(!participantsIds.isEmpty()){	
		
			details = fetchDetails(participantsIds)
                        .stream()
                        .collect(Collectors.toMap(UserDTO::getId, Function.identity()));
		}
		
		//Set default deleted user details
		if(!deletedParticipantsIds.isEmpty()){	
			for(Long participantId : deletedParticipantsIds) {
				
				UserDTO deletedParticipantDetails = 
						UserDTO
						.builder()
						.username(DEFAULT_DELETED_USER_USERNAME)
						.profileImageUrl(DEFAULT_DELETED_USER_IMAGE_URL)
						.deleted(true)
						.build();
				details.put(participantId, deletedParticipantDetails);
			}
		}

		//Match details with participants
        if(!details.isEmpty()) {
			for(ConversationViewDTO conversation : conversations) {
				
				UserDTO recipientDetails = details.get(conversation.getRecipient().getUserId());
				if(recipientDetails!=null) {
					mapper.map(recipientDetails, conversation.getRecipient());
				}
			}
        }
    }

    
    private List<UserDTO> fetchDetails(List<Long> ids){
        return feign.getDetails(ids);
    }




    public UserService(UserFeign feign, ModelMapper mapper) {
        this.feign = feign;
        this.mapper = mapper;
    }

    private final UserFeign feign;
    private final ModelMapper mapper;
	@Value("${deletedUser.default.username}")
    private String DEFAULT_DELETED_USER_USERNAME;
    @Value("${deletedUser.default.profileImageUrl}")
    private String DEFAULT_DELETED_USER_IMAGE_URL;
}