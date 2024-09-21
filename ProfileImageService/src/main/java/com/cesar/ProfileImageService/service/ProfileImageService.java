package com.cesar.ProfileImageService.service;

import com.cesar.ProfileImageService.entity.ProfileImage;
import com.cesar.ProfileImageService.repository.ProfileImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class ProfileImageService {

    public String upload(Long userId, MultipartFile imageMetadata) {

        ProfileImage userImageReference = repo.findById(userId).orElse(null);
        //Default image values
        String name = "DefaultProfileImage";
        String extension = ".png";
        String finalName = name + extension;
        boolean defaultImage = true;

        if(!imageMetadata.isEmpty()){

            //Get extension
            String extensionType = imageMetadata.getContentType();
            extension = extensionType.equals("image/jpeg") ? ".jpg" :
                    extensionType.equals("image/png") ? ".png" : null;

            //Verify action to perform
            if(extension!=null){

                //If user already has not custom image...
                name = userImageReference.isDefaultImage() ?
                        UUID.randomUUID().toString() : //upload new image with random name
                        userImageReference.getName(); //or replace it with same name

                //Perform action in server
                finalName = name + extension;
                File file = new File(profileImagesPath + "\\" + finalName);
                try {
                    imageMetadata.transferTo(file);
                } catch (IOException e) {throw new RuntimeException(e);}

                defaultImage=false;
            }
        }
        //Save user image reference in DB
        userImageReference.setUserId(userId);
        userImageReference.setName(name);
        userImageReference.setExtension(extension);
        userImageReference.setFinalName(finalName);
        userImageReference.setDefaultImage(defaultImage);
        repo.save(userImageReference);

        return profileImagesUrl + "/" + finalName;
    }

    @Autowired
    private ProfileImageRepository repo;
    @Value("${profileImages.absolutePath}")
    private String profileImagesPath;
    @Value("${profileImages.url}")
    private String profileImagesUrl;
}