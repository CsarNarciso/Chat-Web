package com.cesar.Media.service;

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

    public String upload(MultipartFile imageMetadata, String oldPath) {

        //Get extension
        String extension = imageMetadata.getContentType();
        extension = extension.equals("image/jpeg") ? ".jpg" :
                extension.equals("image/png") ? ".png" : null;

        if(extension!=null) {

            //Perform action in server
            String finalName = UUID.randomUUID() + extension;
            File file = new File(profileImagesPath + "\\" + finalName);
            try {
                if(oldPath!=null){
                    Files.deleteIfExists(Path.of(oldPath));
                }
                imageMetadata.transferTo(file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return profileImagesUrl + "/" + finalName;
        }
        return null;
    }
    @Value("${images.profile.absolutePath}")
    private String profileImagesPath;
    @Value("${images.profile.url}")
    private String profileImagesUrl;
}