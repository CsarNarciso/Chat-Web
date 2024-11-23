package com.cesar.User.service;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.multipart.MultipartFile;

import com.cesar.User.dto.CreateRequestDTO;
import com.cesar.User.dto.UpdateRequestDTO;
import com.cesar.User.dto.UserDTO;
import com.cesar.User.entity.User;

@Service
public class UserService {


    public UserDTO create(CreateRequestDTO createRequest) {
    	
        //Upload profile image
    	User entity = mapper.map(createRequest, User.class);
        String profileImageURL = mediaService.upload(createRequest.getImageMetadata(), null);
        entity.setProfileImageUrl(profileImageURL);

        //Save
        return dataService.save(entity);
    }
    
    public UserDTO getById(Long id) {
    	return dataService.getById(id);
    }

    public List<UserDTO> getByIds(List<Long> ids) {
    	return dataService.getByIds(ids);
    }

    public UserDTO updateDetails(Long id, UpdateRequestDTO updateRequest) {
    	
    	//If user exists...
    	UserDTO user = dataService.getById(id); 
    	
    	if(user!=null) {
    		
    		boolean updatePerformed = false;
    		User entityForUpdate = mapper.map(user, User.class);
    		
    		//For each allowed update request field
			for (Field updateRequestField : UpdateRequestDTO.class.getDeclaredFields()) {
				
				//Make private request DTO field accessible
				updateRequestField.setAccessible(true);
				
				//If it's present on request (either null or not)
				Optional<?> updateRequestFieldValue = Optional.empty();
				
				try {
					updateRequestFieldValue = (Optional<?>) updateRequestField.get(updateRequest);
				} catch (IllegalArgumentException | IllegalAccessException e) {e.printStackTrace();}
				
				if(updateRequestFieldValue.isPresent()) {
					
					//Get field (map) from DTO to Entity
					Field entityField = ReflectionUtils.findField(User.class, updateRequestField.getName());
								
					//And set (update) on entity
					try {
						
						entityField.setAccessible(true);
						entityField.set(entityForUpdate, updateRequestFieldValue.get());
						updatePerformed = true;
						
					} catch (IllegalArgumentException | IllegalAccessException e) {e.printStackTrace();}
				}
			}
			//If at least a field update was made
			if(updatePerformed) {
				
				//Update
				user = dataService.save(entityForUpdate);

		        //Event Publisher - User updated
		        kafkaTemplate.send("UserUpdated", user);
			}
			return user;
    	}
    	return null;
    }
    

    public String updateProfileImage(Long id, MultipartFile imageMetadata) {

    	UserDTO user = dataService.getById(id);
    	if(user!=null) {
    		
    		//If either new image is not empty (no bad arguments request),
            // or media service request was performed successful (no fallback)
    		String oldPath = user.getProfileImageUrl();
        	String newImageUrl = mediaService.upload(imageMetadata, oldPath);
            
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

            //Mark as deleted
            User deletedUser = new User();
            deletedUser.setId(id);
            deletedUser.setDeleted(true);
            user = dataService.save(deletedUser);

            //Event Publisher - User Deleted
            kafkaTemplate.send("UserDeleted", id);

            return user;
        }
        return null;
    }



    

    public UserService(UserDataService dataService, KafkaTemplate<String, Object> kafkaTemplate, MediaService mediaService, ModelMapper mapper) {
        this.dataService = dataService;
        this.kafkaTemplate = kafkaTemplate;
        this.mediaService = mediaService;
        this.mapper = mapper;
    }

    private final UserDataService dataService;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final MediaService mediaService;
    private final ModelMapper mapper;
}