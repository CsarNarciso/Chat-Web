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

    
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getById(@PathVariable Long id){
    	
        UserDTO user = service.getById(id);
        
        return user!=null
        		? ResponseEntity
        				.status(HttpStatus.OK)
        				.body(user)
        				
        		: ResponseEntity.notFound().build();
    }
    
    
    @GetMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getByIds(@RequestBody List<Long> ids){
        
    	List<UserDTO> users = service.getByIds(ids);
    	
    	return users!=null
    			
    			? ResponseEntity
    					.status(HttpStatus.OK)
    					.body(users) 
    					
                : ResponseEntity.notFound().build();
    }

    
    @PatchMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateDetails(@PathVariable Long id, @RequestBody UpdateRequestDTO updateRequestFields){
        
    	UserDTO user = service.updateDetails(id, updateRequestFields);
    	
    	return user != null 
    			
    			? ResponseEntity
    					.status(HttpStatus.OK)
    					.body(user)
    					
    			: ResponseEntity.notFound().build();
    }
    

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateProfileImage(
            @PathVariable Long id,
            @RequestParam MultipartFile imageMetadata){
    	
    	String newProfileImageUrl = service.updateProfileImage(id, imageMetadata);
    	
    	return newProfileImageUrl!=null
    			
    			 ? newProfileImageUrl.isEmpty() 
    				? ResponseEntity
    						.status(HttpStatus.SERVICE_UNAVAILABLE)
    						.body("Media service down or unreachable")
    				: ResponseEntity
    					.status(HttpStatus.OK)
    					.body(newProfileImageUrl)
    			
    			: ResponseEntity.notFound().build();
    }
    
    
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> delete(@PathVariable Long id){
    	
    	return service.delete(id) != null 
    			? ResponseEntity.noContent().build() 
    			: ResponseEntity.notFound().build();
    }



    

    public Controller(UserService service){
    	this.service = service;
    }

    private final UserService service;
}
