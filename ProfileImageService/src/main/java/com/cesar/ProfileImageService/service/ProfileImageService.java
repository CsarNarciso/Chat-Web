package com.cesar.ProfileImageService.service;

import com.cesar.ProfileImageService.entity.ProfileImage;
import com.cesar.ProfileImageService.repository.ProfileImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProfileImageService {
    public byte[] upload(Long userId, MultipartFile imageMetadata) {

        String extensionType = imageMetadata.getContentType();
        String extension=null;

        if(extensionType.equals("image/jpeg")) {
            extension=".jpg";
        }
        else if (extensionType.equals("image/png")){
            extension=".png";
        }

        if(extension!=null){
            String name;

            //Verify action to perform in image inside server

            if(repo.findByUserId(userId).isPresent()){
                //replace with same name
                name = repo.findByUserId(userId).get().getName();
            }
            //If not,
            else{
                //upload new image
                name = UUID.randomUUID().toString();
            }

            //Perform action in server
            String finalName = name + extension;
            File file = new File(profileImagesPath + "\\" + finalName);
            try {
                imageMetadata.transferTo(file);
            } catch (IOException e) {throw new RuntimeException(e);}

            //Save entity reference in DB
            ProfileImage entity = ProfileImage
                    .builder()
                    .userId(userId)
                    .name(name)
                    .extension(extension)
                    .path(file.getPath())
                    .build();
            repo.save(entity);

            byte[] imageBytes;
            try {
                return imageBytes = Files.readAllBytes(file.toPath());
            } catch (IOException e) {throw new RuntimeException(e);}
        }
        return null;
    }

    public byte[] getByUserId(Long id){
        Optional<ProfileImage> image = repo.findByUserId(id);
        String path = image.map(ProfileImage::getPath).orElse(null);
        try {
            return Files.readAllBytes(Path.of(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Autowired
    private ProfileImageRepository repo;
    @Value("${profileImages.absolutePath}")
    private String profileImagesPath;
}
