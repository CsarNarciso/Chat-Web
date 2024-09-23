package com.cesar.ImageService.service;

import com.cesar.ImageService.entity.GroupImage;
import com.cesar.ImageService.repository.GroupImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class GroupImageService {

    public String uploadGroupImage(Long groupId, MultipartFile imageMetadata) {

        GroupImage groupImageReference = repo.findByGroupId(groupId);
        //Default image values
        String name = "DefaultGroupImage";
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

                //If group has not custom image...
                name = groupImageReference.isDefaultImage() ?
                        UUID.randomUUID().toString() : //upload new image with random name
                        groupImageReference.getName(); //or replace it with same name

                //Perform action in server
                finalName = name + extension;
                File file = new File(groupImagesPath + "\\" + finalName);
                try {
                    imageMetadata.transferTo(file);
                } catch (IOException e) {throw new RuntimeException(e);}

                defaultImage=false;
            }
        }
        //Save group image reference in DB
        groupImageReference.setGroupId(groupId);
        groupImageReference.setName(name);
        groupImageReference.setExtension(extension);
        groupImageReference.setFinalName(finalName);
        groupImageReference.setDefaultImage(defaultImage);
        repo.save(groupImageReference);

        return groupImagesUrl + "/" + finalName;
    }

    public String getGroupImageUrl(Long groupId){
        return groupImagesUrl + repo.findByGroupId(groupId).getFinalName();
    }

    @Autowired
    private GroupImageRepository repo;
    @Value("${images.group.absolutePath}")
    private String groupImagesPath;
    @Value("${images.group.url}")
    private String groupImagesUrl;
}