package com.cesar.User.service;

import com.cesar.User.feign.ProfileImageFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ProfileImageService {

    public String upload(Long userId, MultipartFile imageMetadata){
        return profileImageFeign.upload(userId, imageMetadata);
    }
    @Autowired
    private ProfileImageFeign profileImageFeign;
}