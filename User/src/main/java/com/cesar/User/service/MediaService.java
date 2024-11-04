package com.cesar.User.service;

import com.cesar.User.feign.MediaFeign;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class MediaService {


    public String upload(MultipartFile imageMetadata, String oldPath) {

        String newImageUrl;

        //If image provided,
        if(!imageMetadata.isEmpty()){
            //And user has already a profile image
            newImageUrl = (oldPath!=null)
                    ? feign.upload(imageMetadata, oldPath)  //Update
                    : DEFAULT_IMAGE_PATH; //First time image upload
        }
        //If not,
        else{
            //And user has no profile image yet
            newImageUrl = (oldPath==null)
                    ? DEFAULT_IMAGE_PATH //First time image upload
                    : null; //No old path provided on update request
        }
        return newImageUrl;
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