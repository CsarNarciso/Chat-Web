package com.cesar.ProfileImageService.controller;

import com.cesar.ProfileImageService.service.ProfileImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/profileImages")
public class Controller {

    @PostMapping("/v1/{userId}")
    public ResponseEntity<?> upload(@PathVariable Long userId, @RequestParam MultipartFile imageMetadata){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(service.upload(userId, imageMetadata));
    }

    @Autowired
    private ProfileImageService service;
}