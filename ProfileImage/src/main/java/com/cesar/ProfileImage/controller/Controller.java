package com.cesar.ProfileImage.controller;

import com.cesar.ProfileImage.service.ProfileImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/profileImages")
public class Controller {

    @PostMapping("/{userId}")
    public ResponseEntity<?> uploadProfileImage(@PathVariable Long userId, @RequestParam MultipartFile imageMetadata){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(profileService.upload(userId, imageMetadata));
    }
    @PostMapping("/{userId}")
    public void updateProfileImage(@PathVariable Long userId, @RequestParam MultipartFile imageMetadata){
        profileService.update(userId, imageMetadata);
    }
    @Autowired
    private ProfileImageService profileService;
}