package com.cesar.UserAPI.service;

import com.cesar.UserAPI.dto.UserDTO;
import com.cesar.UserAPI.entity.User;
import com.cesar.UserAPI.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    public UserDTO create(User user){
        return mapper.map(repo.save(user), UserDTO.class);
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
    private ModelMapper mapper;
    @Autowired
    private UserRepository repo;
}