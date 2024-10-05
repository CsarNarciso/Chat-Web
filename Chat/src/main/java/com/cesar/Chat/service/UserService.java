package com.cesar.Chat.service;

import com.cesar.Chat.dto.UserDTO;
import com.cesar.Chat.feign.UserFeign;
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