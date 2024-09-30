package com.cesar.ImageService.service;

import com.cesar.ImageService.UpdateResponseDTO;
import com.cesar.ImageService.entity.ProfileImage;
import com.cesar.ImageService.repository.ProfileImageRepository;
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

        //Default image values (in case custom image is not provided)
        String name = "DefaultProfileImage";
        String extension = ".png";
        String finalName = name + extension;
        boolean defaultImage = true;

        if(!imageMetadata.isEmpty()){

            //Get extension
            String extensionType = imageMetadata.getContentType();
            extension = extensionType.equals("image/jpeg") ? ".jpg" :
                    extensionType.equals("image/png") ? ".png" : null;

            //Upload new image with random name
            name = UUID.randomUUID().toString();

            //Perform action in server
            finalName = name + extension;
            File file = new File(profileImagesPath + "\\" + finalName);
            try {
                imageMetadata.transferTo(file);
            } catch (IOException e) {throw new RuntimeException(e);}

            defaultImage=false;
        }
        //Save user image reference in DB
        repo.save(
                ProfileImage
                        .builder()
                        .userId(userId)
                        .extension(extension)
                        .name(name)
                        .finalName(finalName)
                        .defaultImage(defaultImage)
                        .build()
        );
        return profileImagesUrl + "/" + finalName;
    }

    public void update(Long userId, MultipartFile imageMetadata) {

        ProfileImage userImageReference = repo.findByUserId(userId);

        if(!imageMetadata.isEmpty()){

            //Get extension
            String extensionType = imageMetadata.getContentType();
            String extension = extensionType.equals("image/jpeg") ? ".jpg" :
                    extensionType.equals("image/png") ? ".png" : null;

            //Verify action to perform
            if(extension!=null){

                //If user already still has custom image...
                String name = userImageReference.isDefaultImage() ?
                        UUID.randomUUID().toString() : //upload new image with random name
                        userImageReference.getName(); //replace it with same name

                //Perform action in server
                String finalName = name + extension;
                File file = new File(profileImagesPath + "\\" + finalName);
                try {
                    imageMetadata.transferTo(file);
                } catch (IOException e) {throw new RuntimeException(e);}

                boolean defaultImage=false;

                //Update user image reference in DB
                repo.save(
                        ProfileImage
                                .builder()
                                .id(userImageReference.getId())
                                .name(name)
                                .extension(extension)
                                .finalName(finalName)
                                .defaultImage(false)
                                .build()
                );
                //Event Publisher - Profile Image updated
                //when user updates its profile image
                //Data for: userId, new image url for User and Conversation Service
                UpdateResponseDTO
                        .builder()
                        .userId(userId)
                        .imageUrl(profileImagesUrl + "/" + finalName)
                        .build();
            }
        }
    }

    @Autowired
    private ProfileImageRepository repo;
    @Value("${images.profile.absolutePath}")
    private String profileImagesPath;
    @Value("${images.profile.url}")
    private String profileImagesUrl;
}