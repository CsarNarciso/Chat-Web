package com.cesar.User.service;

import java.util.List;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import com.cesar.User.dto.UserDTO;
import com.cesar.User.entity.User;
import com.cesar.User.repository.UserRepository;

@Service
public class UserDataService {

    public UserDTO save(User user){
        return mapToDTO(repo.save(user));
    }
    
    public UserDTO getById(Long id){
		User entity = repo.findById(id).orElse(null);
		return mapToDTO(entity);
    }

    public List<UserDTO> getByIds(List<Long> ids){
		List<User> users = repo.findAllById(missingCacheIds);
        return mapToDTOS(users);
    }

    public void delete(User user){
		repo.delete(user);
    }




    private UserDTO mapToDTO(User user){
        return mapper.map(user, UserDTO.class);
    }

    private List<UserDTO> mapToDTOS(List<User> users){
        return users
                .stream()
                .map(u -> mapper.map(u, UserDTO.class))
                .toList();
    }



    public UserService(UserRepository repo, ModelMapper mapper) {
        this.repo = repo;
        this.mediaService = mediaService;
        this.mapper = mapper;
    }

    private final UserRepository repo;
    private final ModelMapper mapper;
}