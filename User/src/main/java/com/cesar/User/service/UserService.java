package com.cesar.User.service;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

import com.cesar.User.dto.*;
import org.modelmapper.ModelMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cesar.User.entity.User;
import com.cesar.User.helper.ReflectionsHelper;

@Service
public class UserService {


    public CreateResponseDTO create(CreateRequestDTO createRequest) {
    	
        //Upload profile image
    	User entity = mapper.map(createRequest, User.class);
        UploadImageResponseDTO uploadImageResponse = mediaService.upload(createRequest.getImageMetadata(), null);
        entity.setProfileImageUrl(uploadImageResponse.getImageUrl());

        //Save
        return CreateResponseDTO
                .builder()
                .userResponse(dataService.save(entity))
                .profileImageUploadResponse(uploadImageResponse)
                .build();
    }
    
    public UserDTO getById(Long id) {
    	return dataService.getById(id);
    }

    public List<UserDTO> getByIds(List<Long> ids) {
    	return dataService.getByIds(ids);
    }

    public UserDTO updateDetails(Long id, UpdateRequestDTO updateRequest) {
    	
    	//If user exists...
    	User entity = dataService.getEntityById(id); 
    	
    	if(entity!=null) {
    		
    		boolean updatePerformed = false;
    		UserDTO updatedUser = mapper.map(entity, UserDTO.class);
    		
    		//For each allowed update request field
			for (Field updateRequestField : reflectionsHelper.getFieldss(UpdateRequestDTO.class)) {
				
				//Make private request DTO field accessible
				updateRequestField.setAccessible(true);
				
				//If it's present on request (either null or not)
				Optional<?> updateRequestFieldValue = Optional.empty();
				
				try {
					updateRequestFieldValue = (Optional<?>) updateRequestField.get(updateRequest);
				} catch (IllegalArgumentException | IllegalAccessException e) {e.printStackTrace();}
				
				if(updateRequestFieldValue.isPresent()) {
					
					//Get field (map) from DTO to Entity
					Field entityField = reflectionsHelper.findField(User.class, updateRequestField.getName());
								
					//And set (update) on entity
					try {
						
						entityField.setAccessible(true);
						entityField.set(entity, updateRequestFieldValue.get());
						updatePerformed = true;
						
					} catch (IllegalArgumentException | IllegalAccessException e) {e.printStackTrace();}
				}
			}
			//If at least a field update was made
			if(updatePerformed) {
				
				//Update
				updatedUser = dataService.save(entity);

		        //Event Publisher - User updated
		        kafkaTemplate.send("UserUpdated", entity);
			}
			return updatedUser;
    	}
    	return null;
    }

    public String updateProfileImage(Long id, MultipartFile imageMetadata) {

    	UserDTO user = dataService.getById(id);

    	if(user!=null) {
    		
    		//If either new image is not empty (no bad arguments request),
            // or media service request was performed successful (no fallback)
    		String oldPath = user.getProfileImageUrl();
        	UploadImageResponseDTO uploadImageResponse = mediaService.upload(imageMetadata, oldPath);
            String newImageUrl = uploadImageResponse.getImageUrl();
            
        	if(newImageUrl!=null && !newImageUrl.equals(oldPath)){

                //Update
        		user.setProfileImageUrl(newImageUrl);
                user = dataService.save(mapper.map(user, User.class));
                
                //Event Publisher - User Updated
                kafkaTemplate.send("UserUpdated", user);

                return user.getProfileImageUrl();
            }
        	return "";
    	}
        return null;
    }

    public UserDTO delete(Long id) {

        UserDTO user = dataService.getById(id);
        if(user!=null){

        	//Remove Profile Image from Media Server
            mediaService.delete(user.getProfileImageUrl());

            //Delete
            dataService.delete(id);

            //Event Publisher - User Deleted
            kafkaTemplate.send("UserDeleted", id);

            return user;
        }
        return null;
    }



    

    public UserService(UserDataService dataService, KafkaTemplate<String, Object> kafkaTemplate, MediaService mediaService, ModelMapper mapper, ReflectionsHelper reflectionsHelper) {
        this.dataService = dataService;
        this.kafkaTemplate = kafkaTemplate;
        this.mediaService = mediaService;
        this.mapper = mapper;
        this.reflectionsHelper = reflectionsHelper;
    }

    private final UserDataService dataService;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final MediaService mediaService;
    private final ModelMapper mapper;
    private final ReflectionsHelper reflectionsHelper;
}