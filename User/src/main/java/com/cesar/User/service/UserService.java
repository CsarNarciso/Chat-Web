package com.cesar.User.service;

import com.cesar.User.dto.CreateRequestDTO;
import com.cesar.User.dto.UpdateDetailsDTO;
import com.cesar.User.dto.UserDTO;
import com.cesar.User.entity.User;
import com.cesar.User.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserService {

    public UserDTO create(CreateRequestDTO userRequest){
        //Store in DB to get user ID
        User user = mapper.map(userRequest, User.class);
        user = repo.save(user);
        //Upload profile image to get URL
        String profileImageURL = profileImageService.upload(user.getId(), userRequest.getProfileImageMetadata());
        user.setProfileImageUrl(profileImageURL);
        //Store in CACHE with TTL
        //Store in DB
        repo.save(user);
        return mapper.map(user, UserDTO.class);
    }
    public UserDTO updateDetails(UpdateDetailsDTO updatedUser){
        //Update user in CACHE if it exists there
        //Update in DB
        return mapper.map(repo.save(
                mapper.map(updatedUser, User.class)), UserDTO.class);
        //Event Publisher - User updated
        //when user details (name, email) is updated
        //Data for: name for conversation service
        //Data for: email for auth server
    }
    //Event Consumer - Profile image updated
    //when user changes its profile image in Image Service
    //Data: userId, newImageUrl
    //Task: update url reference in user entity
    public void updateProfileImageUrl(Long id, String newUrl){
        //Update user in CACHE if it exists there
        //Update in DB
        repo.save(
                User
                        .builder()
                        .id(id)
                        .profileImageUrl(newUrl)
                        .build()
        );
    }
    //Event Consumer - Conversation deleted
    //when user deletes a conversation in Conversation Service
    //Data: userId, conversationId
    //Task: Update conversations ids list in user entity
    public void addConversationId(Long id, Long conversationId){
        //Update user in CACHE
        //Update in DB
        User user = repo.getReferenceById(id);
        List<Long> conversationsIds = user.getConversationsIds();
        conversationsIds.add(conversationId);
        repo.save(
                User
                        .builder()
                        .id(user.getId())
                        .conversationsIds(conversationsIds)
                        .build()
        );
    }
    //Event Consumer - Conversation deleted
    //when user deletes a conversation in Conversation Service
    //Data: userId, conversationId
    //Task: Update conversations ids list in user entity
    public void removeConversationId(Long id, Long conversationId){
        //Update user in CACHE
        //Update in DB
        User user = repo.getReferenceById(id);
        repo.save(
                User
                        .builder()
                        .id(user.getId())
                        .conversationsIds(user.getConversationsIds()
                                .stream()
                                .filter(c -> c.equals(conversationId))
                                .toList())
                        .build()
        );
    }
    public List<UserDTO> getByIds(List<Long> ids){
        //Store users data references in CACHE with TTL
        return repo.findAllById(ids)
                .stream()
                .map( u -> mapper.map(u, UserDTO.class) )
                .toList();
    }
    public UserDTO getById(Long id){
        //Store user data reference in CACHE with TTL
        return mapper.map(repo.getReferenceById(id), UserDTO.class);
    }
    @Autowired
    private UserRepository repo;
    @Autowired
    private ProfileImageService profileImageService;
    @Autowired
    private ModelMapper mapper;
}