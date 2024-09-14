package com.cesar.PresenceService.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PresenceService {

    public int addOnlineUser(String name){
        onlineUsers.add(name);
        return onlineUsers.size() -1;
    }

    public String getOnlineUser(int index){
        return onlineUsers.get(index);
    }

    public List<String> getOnlineUsers(){
        return onlineUsers;
    }

    public String removeOnlineUser(int index){
        String user = onlineUsers.get(index);
        onlineUsers.remove(index);
        return user;
    }


    private List<String> onlineUsers = new ArrayList<>();
}
