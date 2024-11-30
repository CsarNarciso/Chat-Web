package com.cesar.User.service;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import com.cesar.User.dto.UserDTO;
import com.cesar.User.entity.User;
import com.cesar.User.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserDataServiceTest {
	
	@BeforeEach
	public void setup() {	
		this.mockUser = createMockUser(1l);
	}
	
	@Test
	public void givenUser_whenSave_thenReturnsUserDTO() {
		
		//Given 
		User entityWithoutID = createMockUser(null);
		
		//When
		when(repo.save(any(User.class))).thenReturn(mockUser);
		
		when(mapper.map(any(User.class), eq(UserDTO.class)))
			.thenReturn(mapToDTO());
		
		UserDTO userResult = service.save(entityWithoutID);
		
		//Then
		
		//Verify repository interaction
		verify(repo, times(1)).save(any(User.class));
		
		//Verify ModelMapper interaction and capture entity before map to DTO
		ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
		verify(mapper, times(1)).map(userCaptor.capture(), eq(UserDTO.class));
		User capturedSavedEntityBeforeMap = userCaptor.getValue();
		
		//Asserts on captured user
		assertNotNull(capturedSavedEntityBeforeMap);
		assertEquals(ID, capturedSavedEntityBeforeMap.getId());
		assertEquals(USERNAME, capturedSavedEntityBeforeMap.getUsername());
		assertEquals(EMAIL, capturedSavedEntityBeforeMap.getEmail());
		assertEquals(PASSWORD, capturedSavedEntityBeforeMap.getPassword());
		assertEquals(IMAGE_URL, capturedSavedEntityBeforeMap.getProfileImageUrl());
		
		//Asserts on result
		assertNotNull(userResult);
		assertEquals(ID, userResult.getId());
		assertEquals(USERNAME, userResult.getUsername());
		assertEquals(EMAIL, userResult.getEmail());
		assertEquals(IMAGE_URL, userResult.getProfileImageUrl());
	}
	
	@Test
	public void givenUserId_whenGetById_thenReturnsUserDTO() {
		
		//Given
		Long id = ID;
		
		//When
		when(repo.findById( anyLong() )).thenReturn( Optional.of(mockUser) );		
		
		when(mapper.map(any(User.class), eq(UserDTO.class)))
    		.thenReturn(mapToDTO());
		
		UserDTO userResult = service.getById(id);
		
		//Then
		
		//Verify repository interaction
		verify(repo, times(1)).findById( anyLong() );
		
		//Verify ModelMapper interaction and capture entity before map to DTO
		ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
		verify(mapper, times(1)).map(userCaptor.capture(), eq(UserDTO.class) );
		User capturedEntityBeforeMap = userCaptor.getValue();
		
		//Asserts on captured user
		assertNotNull(capturedEntityBeforeMap);
		assertEquals(USERNAME, capturedEntityBeforeMap.getUsername());
		assertEquals(EMAIL, capturedEntityBeforeMap.getEmail());
		assertEquals(PASSWORD, capturedEntityBeforeMap.getPassword());
		assertEquals(IMAGE_URL, capturedEntityBeforeMap.getProfileImageUrl());
		
		//Asserts on result
		assertNotNull(userResult);
		assertEquals(USERNAME, userResult.getUsername());
		assertEquals(EMAIL, userResult.getEmail());
		assertEquals(IMAGE_URL, userResult.getProfileImageUrl());
	}
	
	@Test
	public void givenInvalidUserId_whenGetById_thenReturnsNull() {
		
		//Given
		Long id = 500l;
		
		//When
		when(repo.findById( anyLong() )).thenReturn( Optional.empty() );
		UserDTO userResult = service.getById(id);
		
		//Then
		
		//Verify repository interaction
		verify(repo, times(1)).findById( anyLong() );
		
		//Verify NO ModelMapper interaction
		verify(mapper, times(0)).map(any(), any());
		
		//Asserts on result
		assertNull(userResult);
	}
	
	@Test
	public void givenUserIds_whenGetByIds_thenReturnsListOfUserDTO() {
		
		//Given
		List<Long> ids = this.ids;
		entities = createMockUsers(ids);
		
		//When
		when(repo.findAllById( ids )).thenReturn( entities );
		
		when(mapper.map(any(User.class), eq(UserDTO.class)))
    		.thenReturn(mapToDTO());
		
		List<UserDTO> usersResult = service.getByIds(ids);
		
		//Then
		assertNotNull(usersResult);
		assertFalse(usersResult.isEmpty());
		
		//Verify repository interaction
		verify(repo, times(1)).findAllById( ids );
		
		//Verify ModelMapper interaction and capture entity before map to DTO
		ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
		verify(mapper, times(ids.size())).map(userCaptor.capture(), eq(UserDTO.class) );
		List<User> capturedEntitiesBeforeMap = userCaptor.getAllValues();
		
		for(int i = 0; i<ids.size(); i++) {
			
			User capturedEntityBeforeMap = capturedEntitiesBeforeMap.get(i);
			UserDTO userResult = usersResult.get(i);
			
			//Asserts on captured user
			assertNotNull(capturedEntityBeforeMap);
			assertEquals(USERNAME, capturedEntityBeforeMap.getUsername());
			assertEquals(EMAIL, capturedEntityBeforeMap.getEmail());
			assertEquals(PASSWORD, capturedEntityBeforeMap.getPassword());
			assertEquals(IMAGE_URL, capturedEntityBeforeMap.getProfileImageUrl());
			
			//Asserts on result
			assertNotNull(userResult);
			assertEquals(USERNAME, userResult.getUsername());
			assertEquals(EMAIL, userResult.getEmail());
			assertEquals(IMAGE_URL, userResult.getProfileImageUrl());
		}
	}
	
	@Test
	public void givenInvalidUserIds_whenGetByIds_thenReturnsEmptyList() {
		
		//Given
		List<Long> ids = List.of(7l, 8l, 9l);
		
		//When
		when(repo.findAllById( ids )).thenReturn( Collections.emptyList() );
		List<UserDTO> usersResult = service.getByIds(ids);
		
		//Then
		
		//Verify repository interaction
		verify(repo, times(1)).findAllById( ids );
		
		//Verify NO ModelMapper interaction
		verify(mapper, times(0)).map(any(), any());
		
		//Asserts on result
		assertNull(usersResult);
	}
	

	
	private User createMockUser(Long id) {
		return new User(id, USERNAME, EMAIL, PASSWORD, IMAGE_URL);
	}
	
	private List<User> createMockUsers(List<Long> ids){
		return ids
				.stream()
				.map(this::createMockUser)
				.toList();
	}
	
	private UserDTO mapToDTO(){
		return new UserDTO(mockUser.getId(), mockUser.getUsername(), mockUser.getEmail(), mockUser.getProfileImageUrl());
	}
	
	
	private User mockUser;
	private final Long ID = 1l;
	private final String USERNAME = "Username";
	private final String EMAIL = "Email";
	private final String PASSWORD = "PASSWORD";
	private final String IMAGE_URL = "IMAGE_URL";
	
	private List<Long> ids = List.of(1l, 3l, 4l);
	private List<User> entities = createMockUsers(ids);
	
	@Mock
	private UserRepository repo;
	@Mock
	private ModelMapper mapper;
	@InjectMocks
	private UserDataService service;
}