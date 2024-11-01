package com.cesar.Media.controller;

import com.cesar.Media.service.MediaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/media")
public class Controller {

    @PostMapping
    public ResponseEntity<?> upload(@RequestParam MultipartFile imageMetadata, @RequestParam String oldPath){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(profileService.upload(imageMetadata, oldPath));
    }

    @DeleteMapping()
    public void delete(@RequestParam String path) {
        profileService.delete(path);
    }




    public Controller(MediaService profileService){
    	this.profileService = profileService;
    }

    private final MediaService profileService;
}
