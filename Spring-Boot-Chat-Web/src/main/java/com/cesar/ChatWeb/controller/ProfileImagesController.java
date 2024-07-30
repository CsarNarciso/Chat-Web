package com.cesar.ChatWeb.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cesar.ChatWeb.dto.ProfileImageDTO;
import com.cesar.ChatWeb.service.ProfileImageService;

@RestController
@RequestMapping(value="/api.profileImages", consumes=MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
public class ProfileImagesController {

	
	@PostMapping()
	public ResponseEntity<?> save(@RequestBody ProfileImageDTO profileImage) {
		
		//Return saved image in DB as DTO.
		return ResponseEntity.status(HttpStatus.OK)
				.body(service.save(profileImage));
	}
	
	
	@GetMapping("{imageName}")
	public ResponseEntity<?> getByName(@RequestParam String imageName) {

		ProfileImageDTO image = service.getByName(imageName);
		
		//If resource exists...
		if (image != null) {
			
			return ResponseEntity
				.status(HttpStatus.OK)
				.body(image);
		}
		
		//if not,
		return ResponseEntity
				.status(HttpStatus.NO_CONTENT)
				.body("Image not found");
	}
	
	
	
	private ProfileImageService service;
}
