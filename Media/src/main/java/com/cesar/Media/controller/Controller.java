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

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> upload(@RequestPart MultipartFile imageMetadata, @RequestParam(required = false) String oldPath) throws Exception{
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.upload(imageMetadata, oldPath));
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
