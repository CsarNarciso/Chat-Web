package com.cesar.ProfileImageService.controller;

import com.cesar.ProfileImageService.service.ProfileImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/profileImages.api")
public class Controller {

    @PostMapping(value="/v1/{userId}", produces=MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<?> getByUserId(@PathVariable Long userId, @RequestParam MultipartFile imageMetadata){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .contentType(MediaType.IMAGE_JPEG)
                .body(service.upload(userId, imageMetadata));
    }

    @GetMapping(value="/v1/{userId}", produces=MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<?> getByUserId(@PathVariable Long userId){
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.IMAGE_JPEG)
                .body(service.getByUserId(userId));
    }

    @Autowired
    private ProfileImageService service;
}