package com.cesar.User.controller;

import com.cesar.User.dto.CreateRequestDTO;
import com.cesar.User.dto.UpdateDetailsDTO;
import com.cesar.User.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/users")
public class Controller {

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateRequestDTO createRequest){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(service.create(createRequest));
    }
    @PutMapping
    public ResponseEntity<?> updateDetails(@RequestBody UpdateDetailsDTO updatedDetails){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(service.updateDetails(updatedDetails));
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id){
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(service.getById(id));
    }
    @GetMapping
    public ResponseEntity<?> getByIds(@RequestBody List<Long> ids){
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(service.getByIds(ids));
    }
    @Autowired
    private UserService service;
}