package com.cesar.UserAPI.service;

import com.cesar.UserAPI.dto.UserDTO;
import com.cesar.UserAPI.entity.User;
import com.cesar.UserAPI.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    public UserDTO create(User user) {

        user = repo.save(user);
        UserDTO userDTO = mapper.map(user, UserDTO.class);
        return userDTO;
    }

    public List<UserDTO> getAll(){

        List<User> users = repo.findAll();

        if(!users.isEmpty()) {
            List<UserDTO> usersDTO = users
                    .stream()
                    .map( u -> mapper.map(u, UserDTO.class) )
                    .toList();

            return usersDTO;
        }
        return null;
    }

    public UserDTO getById(Long id){

        Optional<User> user = repo.findById(id);

        if (user.isPresent()) {
            UserDTO userDTO = mapper.map(user.get(), UserDTO.class);
            return userDTO;
        }
        return null;
    }



    @Autowired
    private ModelMapper mapper;
    @Autowired
    private UserRepository repo;
}
