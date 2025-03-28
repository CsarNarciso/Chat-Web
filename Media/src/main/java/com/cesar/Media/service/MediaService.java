package com.cesar.Media.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class MediaService {

    public String upload(MultipartFile imageMetadata) throws Exception {
        
    	if(!imageMetadata.isEmpty()){
        	
            //Get extension
            String extension = imageMetadata.getContentType();
            extension = extension.equals("image/jpeg") ? ".jpg" :
                    extension.equals("image/png") ? ".png" : null;

            if(extension!=null) {
            	
                //Perform action in server
                String finalName = UUID.randomUUID() + extension;
                File file = new File(mediaPath + "\\" + finalName);
                try {
                    imageMetadata.transferTo(file);
                    
                } catch (IOException e) {
                	throw new RuntimeException("Error saving new file: " + e);
                }
                return String.format("%s/%s", mediaBaseUrl, finalName);
            }
            throw new BadRequestException("File extension not supported.");
        }
        throw new BadRequestException("Image file not provided.");
    }

    public void delete(String path) {
        try {
        	boolean deleted = Files.deleteIfExists(Path.of( mediaPath + "\\" + path.substring(path.lastIndexOf("/")+1)));
        	
        	if(!deleted) {
        		throw new RuntimeException("File not found");
        	}
        } catch (IOException e) {
            throw new RuntimeException("Error deleting file: " + e);
        }
    }



    public MediaService(@Value("${media.dirName}") String mediaDirName, @Value("${media.url}") String mediaBaseUrl){
        this.mediaBaseUrl = mediaBaseUrl;
        mediaPath = Paths.get("").toAbsolutePath().resolve("Media/" + mediaDirName).toString();
    }

    private final String mediaPath;
    private final String mediaBaseUrl;
}