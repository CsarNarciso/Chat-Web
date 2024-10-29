package com.cesar.User.service;

import com.cesar.User.feign.MediaFeign;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class MediaService {


    public String upload(MultipartFile imageMetadata, String oldPath) {
        return imageMetadata.isEmpty() ? DEFAULT_IMAGE_PATH : feign.upload(imageMetadata, oldPath);
    }

    public void delete(String path){
        if(!path.equals(DEFAULT_IMAGE_PATH)){
            feign.delete(path);
        }
    }



    public MediaService(MediaFeign feign) {
        this.feign = feign;
    }
    private final MediaFeign feign;

    @Value("${media.defaultImage.path}")
    private String DEFAULT_IMAGE_PATH;
}