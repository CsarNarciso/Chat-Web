package com.cesar.ImageService.controller;

import com.cesar.ImageService.service.GroupImageService;
import com.cesar.ImageService.service.ProfileImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/images")
public class Controller {

    @PostMapping("/v1/{userId}")
    public ResponseEntity<?> uploadProfileImage(@PathVariable Long userId, @RequestParam MultipartFile imageMetadata){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(profileService.uploadProfileImage(userId, imageMetadata));
    }

    @GetMapping("/v1/{userId}")
    public ResponseEntity<?> getProfileImageUrl(@PathVariable Long userId){
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(profileService.getProfileImageUrl(userId));
    }

    @PostMapping("/v1/{groupId}")
    public ResponseEntity<?> uploadGroupImage(@PathVariable Long groupId, @RequestParam MultipartFile imageMetadata){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(groupService.uploadGroupImage(groupId, imageMetadata));
    }

    @GetMapping("/v1/{groupId}")
    public ResponseEntity<?> getGroupImageUrl(@PathVariable Long groupId){
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(groupService.getGroupImageUrl(groupId));
    }

    @Autowired
    private ProfileImageService profileService;
    @Autowired
    private GroupImageService groupService;
}