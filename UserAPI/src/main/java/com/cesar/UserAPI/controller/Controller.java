package com.cesar.UserAPI.controller;

import com.cesar.UserAPI.dto.UserDTO;
import com.cesar.UserAPI.entity.User;
import com.cesar.UserAPI.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/users.api.v1")
public class Controller {

    @PostMapping
    public ResponseEntity<?> create(@RequestBody User user){

        UserDTO userDTO = service.create(user);
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(userDTO);
    }

    @GetMapping
    public ResponseEntity<?> getAllUsers(){

        List<UserDTO> users = service.getAll();

        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id){

        UserDTO user = service.getById(id);

        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(user);
    }

    @Autowired
    private UserService service;
}
