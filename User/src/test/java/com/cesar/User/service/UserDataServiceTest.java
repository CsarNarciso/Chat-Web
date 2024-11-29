package com.cesar.User.service;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import com.cesar.User.dto.UserDTO;
import com.cesar.User.entity.User;
import com.cesar.User.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserDataServiceTest {

	@Mock
	private UserRepository repo;
	@Mock
	private ModelMapper mapper;
	@InjectMocks
	private UserDataService service;
	
	private User entity;
	private List<User> entities;
	
	@BeforeEach
	public void init() {
		this.entity = new User(1l, "Username", "Email", "Password", "AvatarUrl");
		when(mapper.map(any(User.class), eq(UserDTO.class)))
        .thenReturn(new UserDTO(entity.getId(), entity.getUsername(), entity.getEmail(), entity.getProfileImageUrl()));
	}
	
	@Test
	public void getById() {
		
		//Given
		Long id = 1l;
		
		//When
		when(repo.findById( anyLong() )).thenReturn( Optional.of(entity) );
		UserDTO user = service.getById(id);
		
		//Then
		assertNotNull(user);
		assertEquals(entity.getUsername(), user.getUsername());
		assertEquals(entity.getEmail(), user.getEmail());
		assertEquals(entity.getProfileImageUrl(), user.getProfileImageUrl());
		verify(repo).findById( anyLong() );
		verify(mapper).map(entity, UserDTO.class);
	}
	
	@Test
	public void getByIds() {
		
		//Given
		List<Long> ids = List.of(1l, 3l, 85l);
		entities = List.of(
				entity,
				new User(3l, "Username", "Email", "Password", "AvatarUrl"),
				new User(5l, "Username", "Email", "Password", "AvatarUrl"),
				new User(86l, "Username", "Email", "Password", "AvatarUrl")
				);		
		//When
		when(repo.findAllById( (List<Long>) any() )).thenReturn( entities );
		List<UserDTO> users = service.getByIds(ids);
		
		//Then
		assertNotNull(users);
		assertFalse(users.isEmpty());
		assertEquals(entities.get(0).getUsername(), users.get(0).getUsername());
		assertEquals(entities.get(0).getEmail(), users.get(0).getEmail());
		assertEquals(entities.get(0).getProfileImageUrl(), users.get(0).getProfileImageUrl());
		verify(repo).findAllById( anyList() );
	}
}