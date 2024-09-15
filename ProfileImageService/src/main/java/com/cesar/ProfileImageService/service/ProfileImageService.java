package com.cesar.ProfileImageService.service;

import com.cesar.ProfileImageService.dto.UploadImageDTO;
import com.cesar.ProfileImageService.dto.UserDTO;
import com.cesar.ProfileImageService.feign.FeignUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Service
public class ProfileImageService {
    public UploadImageDTO upload(Long userId, MultipartFile imageMetadata) {

        UserDTO user = feignUser.getById(userId);
        String extensionType = imageMetadata.getContentType();
        String extension =
                extensionType.equals("image/jpeg") ? ".jpg" :
                extensionType.equals("image/png") ? ".png" : null;

        //Verify action to perform
        if(extension!=null){
            //If already has image...
            String actionPerformed = user.isHasImage() ?
                    "Replace" :
                    "Create";

            String name = actionPerformed.equals("Replace") ?
                    user.getImageName() : //replace it with same name
                    UUID.randomUUID().toString(); //if not, upload new image with random name

            //Perform action in server
            String finalName = name + extension;
            File file = new File(profileImagesPath + "\\" + finalName);
            try {
                imageMetadata.transferTo(file);
            } catch (IOException e) {throw new RuntimeException(e);}

            //Upload changes in user API
            user = UserDTO
                    .builder()
                    .hasImage(true)
                    .imageName(name)
                    .imageExtension(extension)
                    .imagePath(finalName)
                    .build();
            feignUser.update(userId, user);

            byte[] imageBytes;
            try {
                imageBytes = Files.readAllBytes(file.toPath());
            } catch (IOException e) {throw new RuntimeException(e);}

            return UploadImageDTO
                    .builder()
                    .imageBytes(imageBytes)
                    .actionPerformed(actionPerformed)
                    .build();
        }
        return null;
    }

    public byte[] getByUserId(Long id){
        UserDTO user = feignUser.getById(id);
        String path = profileImagesPath + user.getImagePath();
        try {
            return Files.readAllBytes(Path.of(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Autowired
    private FeignUser feignUser;
    @Value("${profileImages.absolutePath}")
    private String profileImagesPath;
}