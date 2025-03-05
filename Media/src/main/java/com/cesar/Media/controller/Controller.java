package com.cesar.Media.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.cesar.Media.service.MediaService;

@RestController
@RequestMapping("/media")
public class Controller {

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> upload(@RequestPart MultipartFile imageMetadata) throws Exception{
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.upload(imageMetadata));
    }

    @DeleteMapping
    public void delete(@RequestParam String path) {
        service.delete(path);
    }




    public Controller(MediaService profileService){
    	this.service = profileService;
    }

    private final MediaService service;
}
