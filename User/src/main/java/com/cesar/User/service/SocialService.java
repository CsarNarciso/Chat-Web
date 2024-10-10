package com.cesar.User.service;

import com.cesar.User.dto.RelationshipDTO;
import com.cesar.User.feign.SocialFeign;
import org.springframework.beans.factory.annotation.Autowired;

public class SocialService {

    public RelationshipDTO getUserRelationships(Long userId){
        return feign.getUserRelationships(userId);
    }
    @Autowired
    private SocialFeign feign;
}