package com.cesar.ChatWeb.service;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.cesar.ChatWeb.dto.ProfileImageDTO;
import com.cesar.ChatWeb.model.ProfileImage;
import com.cesar.ChatWeb.repository.ProfileImageRepository;

@Service
public class ProfileImageService {

	
	public ProfileImageDTO save(ProfileImageDTO image) {
		
		//Save image throw DTO in DB,
		ProfileImage imageEntity = repo.save(dtoMapper.map(image, ProfileImage.class)); //mapping it to entity.
		
		//If resource exists..
		if (imageEntity != null) {
			
			//map recent saved entity to DTO, return it.
			return dtoMapper.map(imageEntity, ProfileImageDTO.class); 
		}
		
		return null;
	}
	
	
	public ProfileImageDTO getByName(String name) {
		
		//Get resource from DB.
		ProfileImage imageEntity = repo.findByName(name);
	
		//If it exists...
		if (imageEntity != null) {
			
			//map to DTO.
			return dtoMapper.map(imageEntity, ProfileImageDTO.class);
		}
		
		return null;
	}
	
	
	
	
	private ProfileImageRepository repo;
	private ModelMapper dtoMapper;
}
