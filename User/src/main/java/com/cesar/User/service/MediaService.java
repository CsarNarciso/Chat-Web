package com.cesar.User.service;

import com.cesar.User.exception.CustomBadRequestException;
import com.cesar.User.exception.CustomInternalServerErrorException;
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

        String uploadedImageUrl = (oldPath!=null && !oldPath.isEmpty()) ? oldPath : DEFAULT_IMAGE_URL;
        HttpStatusCode statusCode = HttpStatus.CREATED;
        String message = "Image successfully uploaded";

    	//Either update image, or first time custom image upload
        try{
            uploadedImageUrl = feign.upload(imageMetadata);
        }
        //If Media Service fails
        catch(CustomInternalServerErrorException | CustomBadRequestException ex){

            if(ex.getClass() == CustomInternalServerErrorException.class){
                statusCode = ((CustomInternalServerErrorException) ex).getStatusCode();
            }
            statusCode = ((CustomBadRequestException) ex).getStatusCode();

            if(uploadedImageUrl.equals(DEFAULT_IMAGE_URL)){
                message = "Error occurred in Media Service. Using default image: " + ex.getMessage();
            }
            message = "Error occurred in Media Service. No changes made: " + ex.getMessage();
        }

        return UploadImageResponseDTO
                .builder()
                .httpStatusCode(statusCode)
                .message(message)
                .imageUrl(uploadedImageUrl)
                .build();
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