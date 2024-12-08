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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("/users")
public class Controller {
	
    @Operation(
		summary = "Create User",
		description = "Create new user and return the created one.",
		requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
			description = "Details to create User",
			required = true
		),
		responses = {
			@ApiResponse(
				responseCode = "201",
				description = "User created",
				content = @Content(
					mediaType = "application/json",
					schema = @Schema(implementation = UserDTO.class)
				)
			),
			@ApiResponse(
				responseCode = "400",
				description = "Bad request arguments"
			)
		}
    )
	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> create(@ModelAttribute CreateRequestDTO createRequest){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.create(createRequest));
    }

    
	@Operation(
		summary = "Get User by ID",
		description = "Search and get a User using its ID",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "User retrieved",
				content = @Content(
					mediaType = "application/json",
					schema = @Schema(implementation = UserDTO.class)
				)
			),
			@ApiResponse(
				responseCode = "404",
				description = "User not found"
			),
			@ApiResponse(
				responseCode = "400",
				description = "Bad request arguments"
			)
		}
    )
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id){
    	
        UserDTO user = service.getById(id);
        
        return user!=null
        		? ResponseEntity
        				.status(HttpStatus.OK)
        				.body(user)
        				
        		: ResponseEntity.notFound().build();
    }
    
    
	@Operation(
		summary = "Get Users by IDs",
		description = "Search and get a list of Users using its IDs",
		parameters = {
				@Parameter(name = "ids", description = "List of User IDs", example = "{1, 3, 4}")
		},
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "At least one User retrieved",
				content = @Content(
					mediaType = "application/json",
					schema = @Schema(example = "List of UserDTO objects")
				)
			),
			@ApiResponse(
				responseCode = "404",
				description = "No Users found"
			),
			@ApiResponse(
				responseCode = "400",
				description = "Bad request arguments"
			)
		}
    )
    @GetMapping
    public ResponseEntity<?> getByIds(@RequestParam List<Long> ids){
        
    	List<UserDTO> users = service.getByIds(ids);
    	
    	return users!=null
    			
    			? ResponseEntity
    					.status(HttpStatus.OK)
    					.body(users) 
    					
                : ResponseEntity.notFound().build();
    }

    @Operation(
		summary = "Update User details",
		description = "Update specific User details attributes and return the updated one.",
		requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
			description = "User details to update",
			required = true,
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = UpdateRequestDTO.class),
				examples = @ExampleObject(
						value = """
							{ 
								"username": "Username", 
								"email": "email@gmail.com" 
							}
							""")
			)
		),
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "User updated",
				content = @Content(
					mediaType = "application/json",
					schema = @Schema(implementation = UserDTO.class)
				)
			),
			@ApiResponse(
				responseCode = "404",
				description = "User not found"
			),
			@ApiResponse(
				responseCode = "400",
				description = "Bad request arguments"
			)
		}
    )
    @PatchMapping("/{id}")
    public ResponseEntity<?> updateDetails(@PathVariable Long id, @RequestBody UpdateRequestDTO updateRequestFields){
        
    	UserDTO user = service.updateDetails(id, updateRequestFields);
    	
    	return user != null 
    			
    			? ResponseEntity
    					.status(HttpStatus.OK)
    					.body(user)
    					
    			: ResponseEntity.notFound().build();
    }
    
	@Operation(
		summary = "Update User profile image",
		description = "Upload new image file and return User's updated profile image URL",
		requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
			description = "Image file with extension jpg or png"
		),
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "Profile image updated",
				content = @Content(
					mediaType = "text/plain",
					schema = @Schema(example = "http://mediaService:port/media/random-image-name.extension")
				)
			),
			@ApiResponse(
				responseCode = "503",
				description = "Media Service unavailable. No updates made",
				content = @Content(
					mediaType = "text/plain",
					schema = @Schema(example = "Media service down or unreachable")
				)
			),
			@ApiResponse(
				responseCode = "404",
				description = "User not found"
			),
			@ApiResponse(
				responseCode = "400",
				description = "Bad request arguments"
			)
		}
    )
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateProfileImage(
            @PathVariable Long id,
            @RequestParam MultipartFile imageMetadata){
    	
    	String newProfileImageUrl = service.updateProfileImage(id, imageMetadata);
    	
    	return newProfileImageUrl!=null
    			
    			 ? newProfileImageUrl.isEmpty() 
    				? ResponseEntity
    						.status(HttpStatus.SERVICE_UNAVAILABLE)
    						.contentType(MediaType.TEXT_PLAIN)
    						.body("Media service down or unreachable")
    				: ResponseEntity
    					.status(HttpStatus.OK)
    					.contentType(MediaType.TEXT_PLAIN)
    					.body(newProfileImageUrl)
    			
    			: ResponseEntity.notFound().build();
    }
    
    @Operation(
		summary = "Delete User by ID",
		description = "Search and delete a User using its ID",
		responses = {
			@ApiResponse(
				responseCode = "204",
				description = "User deleted"
			),
			@ApiResponse(
				responseCode = "404",
				description = "User not found"
			),
			@ApiResponse(
				responseCode = "400",
				description = "Bad request arguments"
			)
		}
    )
    @DeleteMapping("/{id}")
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
