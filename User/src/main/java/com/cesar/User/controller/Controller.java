package com.cesar.User.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.cesar.User.dto.CreateRequestDTO;
import com.cesar.User.dto.UpdateRequestDTO;
import com.cesar.User.dto.UserDTO;
import com.cesar.User.service.UserService;

@RestController
@RequestMapping("/users")
public class Controller {

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> create(@ModelAttribute CreateRequestDTO createRequest){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.create(createRequest));
    }

    @GetMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getById(@PathVariable Long id){
    	
        UserDTO user = service.getById(id);
        
        return user!=null
        		? ResponseEntity
        				.status(HttpStatus.OK)
        				.body(user)
        				
        		: ResponseEntity.noContent().build();
    }
    
    @GetMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getByIds(@RequestBody List<Long> ids){
        
    	List<UserDTO> users = service.getByIds(ids);
    	
    	return !users.isEmpty()
    			
    			? ResponseEntity
    					.status(HttpStatus.OK)
    					.body(users) 
    					
                : ResponseEntity.noContent().build();
    }

    @PatchMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateDetails(@PathVariable Long id, @RequestBody UpdateRequestDTO updateRequestFields){
        
    	UserDTO user = service.updateDetails(id, updateRequestFields);
    	
    	return user != null 
    			
    			? ResponseEntity
    					.status(HttpStatus.OK)
    					.body(user)
    					
    			: ResponseEntity.noContent().build();
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateProfileImage(
            @PathVariable Long id,
            @RequestParam MultipartFile imageMetadata,
            @RequestParam(required = true) String oldPath){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(service.updateProfileImage(id, imageMetadata, oldPath));
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> delete(@PathVariable Long id){
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(service.delete(id));
    }




    public Controller(UserService service){
    	this.service = service;
    }

    private final UserService service;
}
