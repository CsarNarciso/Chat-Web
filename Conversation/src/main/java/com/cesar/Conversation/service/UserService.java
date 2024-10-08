package com.cesar.Conversation.service;

import com.cesar.Conversation.dto.UserDTO;
import com.cesar.Conversation.feign.UserFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserService {

    public List<UserDTO> getUsersDetails(List<Long> usersIds) {
        return userFeign.getDetails(usersIds);
    }
    @Autowired
    private UserFeign userFeign;
}