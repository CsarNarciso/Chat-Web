package com.cesar.User.controller;

import com.cesar.User.dto.CreateRequestDTO;
import com.cesar.User.dto.UpdateRequestDTO;
import com.cesar.User.dto.UserDTO;
import com.cesar.User.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/users")
public class Controller {

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> create(@ModelAttribute CreateRequestDTO createRequest){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(service.create(createRequest));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id){
    	
        UserDTO user = service.getById(id);
        
        return user!=null
        		? ResponseEntity
        				.status(HttpStatus.OK)
        				.contentType(MediaType.APPLICATION_JSON)
        				.body(user)
        		: ResponseEntity.noContent().build();
    }
    
    @GetMapping
    public ResponseEntity<?> getByIds(@RequestBody List<Long> ids){
        
    	List<UserDTO> users = service.getByIds(ids);
    	
    	return !users.isEmpty()
    			
    			? ResponseEntity
    					.status(HttpStatus.OK)
    					.contentType(MediaType.APPLICATION_JSON)
    					.body(users) 
                : ResponseEntity.noContent().build();
    }

    @PutMapping
    public ResponseEntity<?> updateDetails(@RequestBody UpdateRequestDTO updatedDetails){
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(service.updateDetails(updatedDetails));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProfileImage(
            @PathVariable Long id,
            @RequestParam MultipartFile imageMetadata,
            @RequestParam String oldPath){
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(service.updateProfileImage(id, imageMetadata, oldPath));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .contentType(MediaType.APPLICATION_JSON)
                .body(service.delete(id));
    }




    public Controller(UserService service){
    	this.service = service;
    }

    private final UserService service;
}
