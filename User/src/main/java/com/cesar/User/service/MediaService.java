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
        	
        	//Try to upload
        	String uploadedImageUrl = feign.upload(imageMetadata, oldPath);
        	
        	//And if user has already an image
        	newImageUrl = (oldPath!=null) 
        			? uploadedImageUrl //Update
        			//Or, if server response is successful
        			: (uploadedImageUrl != oldPath) 
        					? uploadedImageUrl //First time custom image upload
        					: DEFAULT_IMAGE_URL; //Default image
        }
        //If not,
        else{
            //And user has no profile image yet
            newImageUrl = (oldPath==null)
                    ? DEFAULT_IMAGE_URL //First time default image upload
                    : null; //Update bad request error. No old path provided
        }
        return newImageUrl;
    }

    public void delete(String path){
        if(!path.equals(DEFAULT_IMAGE_URL)){
            feign.delete(path);
        }
    }



    public MediaService(MediaFeign feign) {
        this.feign = feign;
    }
    private final MediaFeign feign;

    @Value("${defaultImage.url}")
    private String DEFAULT_IMAGE_URL;
}