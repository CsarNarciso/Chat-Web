package com.cesar.Media.controller;

import com.cesar.Media.service.MediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/media")
public class Controller {
    @PostMapping
    public ResponseEntity<?> uploadProfileImage(@RequestParam MultipartFile imageMetadata, @RequestParam String oldPath){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(profileService.upload(imageMetadata, oldPath));
    }
    @Autowired
    private MediaService profileService;
}