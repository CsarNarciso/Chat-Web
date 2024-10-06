package com.cesar.User.service;

import com.cesar.User.dto.*;
import com.cesar.User.entity.User;
import com.cesar.User.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@Service
public class UserService {

    public UserDTO create(CreateRequestDTO createRequest){
        User user = mapper.map(createRequest, User.class);
        //Upload profile image
        String profileImageURL = mediaService.upload(createRequest.getProfileImageMetadata(), null);
        //Store URL in user entity
        user.setProfileImageUrl(profileImageURL);
        return mapper.map(repo.save(user), UserDTO.class);
    }
    public UpdateResponseDTO updateDetails(UpdateRequestDTO updateRequest){
        return mapper.map(repo.save(
                mapper.map(updateRequest, User.class)), UpdateResponseDTO.class);
        //Event Publisher - User updated
        //when user details (username, email) is updated
        //Data for: username for Conversation Service
        //Data for: email for Auth Server
    }
    public String updateProfileImage(Long id, MultipartFile imageMetadata, String oldPath){
        return repo.save(
                User
                        .builder()
                        .id(id)
                        .profileImageUrl(mediaService.upload(imageMetadata, oldPath))
                        .build()
        ).getProfileImageUrl();
        //Event Publisher - User Profile Image Updated
        //when user image is updated
        //Data for: new image url for Conversation Service
    }

    public List<UserDTO> getByIds(List<Long> ids){
        return repo.findAllById(ids)
                .stream()
                .map( u -> mapper.map(u, UserDTO.class) )
                .toList();
    }
    public UserDTO getById(Long id){
        return mapper.map(repo.getReferenceById(id), UserDTO.class);
    }
    @Autowired
    private UserRepository repo;
    @Autowired
    private MediaService mediaService;
    @Autowired
    private ModelMapper mapper;
}