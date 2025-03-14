package com.cesar.User.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cesar.User.dto.UploadImageResponseDTO;
import com.cesar.User.feign.MediaFeign;

@Service
public class MediaService {


    public UploadImageResponseDTO upload(MultipartFile imageMetadata, String oldPath) {

        String uploadedImageUrl = (oldPath!=null) ? oldPath : DEFAULT_IMAGE_URL;
        
    	//Either update image, or first time custom image upload
    	try{
    		
    		uploadedImageUrl = feign.upload(imageMetadata);
    		return UploadImageResponseDTO
    				.builder()
    				.httpStatusCode(HttpStatus.CREATED)
    				.message("Image successfully uploaded")
    				.imageUrl(uploadedImageUrl)
    				.build();
    		
    	}catch(Exception ex) {
    		return UploadImageResponseDTO
    				.builder()
    				.httpStatusCode(ex.)
    				.message(ex.getMessage())
    				.imageUrl(uploadedImageUrl)
    				.build();
    	}
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