package com.cesar.User.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.multipart.MultipartFile;
import com.cesar.User.dto.CreateRequestDTO;
import com.cesar.User.dto.UserDTO;
import com.cesar.User.entity.User;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

	@Test
	public void givenFullCreateRequest_whenCreate_thenReturnsUserDTOWithCustomImageUrl() {
		
		//Given
		CreateRequestDTO createRequest = new CreateRequestDTO(USERNAME, EMAIL, PASSWORD, IMAGE_FILE);
		
		//When
		when(mapper.map( any(CreateRequestDTO.class), eq(User.class)))
			.thenReturn(new User(null, USERNAME, EMAIL, PASSWORD, null));
		
		when(mediaService.upload( any(MultipartFile.class), eq(null))).thenReturn(IMAGE_URL);
		
		when(dataService.save( any(User.class) )).thenReturn(new UserDTO(ID, USERNAME, EMAIL, IMAGE_URL));
		
		UserDTO userResult = service.create(createRequest);
		
		//Then
		
		//Verify Data Service interaction and capture User entity
		ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
		verify(dataService, times(1)).save(userCaptor.capture());
		User entityBeforeSave = userCaptor.getValue();
		
		//Verify ModelMapper interaction
		verify(mapper, times(1)).map(any(CreateRequestDTO.class), eq(User.class));
		
		//Verify Media Service interaction
		verify(mediaService, times(1)).upload(any(MultipartFile.class), eq(null));
		
		//Asserts on entity before save
		assertNotNull(entityBeforeSave);
		assertEquals(null, entityBeforeSave.getId());
		assertEquals(USERNAME, entityBeforeSave.getUsername());
		assertEquals(EMAIL, entityBeforeSave.getEmail());
		assertEquals(IMAGE_URL, entityBeforeSave.getProfileImageUrl());
		
		//Asserts on result
		assertNotNull(userResult);
		assertEquals(ID, userResult.getId());
		assertEquals(USERNAME, userResult.getUsername());
		assertEquals(EMAIL, userResult.getEmail());
		assertEquals(IMAGE_URL, userResult.getProfileImageUrl());
	}
	
	@Test
	public void givenUserId_whenGetById_thenReturnsUserDTO() {
		
		//When
		when(dataService.getById( any(Long.class) )).thenReturn(new UserDTO(ID, USERNAME, EMAIL, IMAGE_URL));
		UserDTO userResult = service.getById(ID);
		
		//Then
		
		//Verify Data Service interaction and capture User entity
		verify(dataService, times(1)).getById(any(Long.class));
		
		//Verify NO ModelMapper interaction
		verify(mapper, times(0)).map(any(), any());
		
		//Asserts on result
		assertNotNull(userResult);
		assertEquals(ID, userResult.getId());
		assertEquals(USERNAME, userResult.getUsername());
		assertEquals(EMAIL, userResult.getEmail());
		assertEquals(IMAGE_URL, userResult.getProfileImageUrl());
	}
	
	@Test
	public void givenUserIds_whenGetByIds_thenReturnsListOfUserDTOs() {
		
		//Given
		List<Long> ids = List.of(1l, 2l, 3l);
		
		//When
		when(dataService.getByIds( ids )).thenReturn(List.of(
						new UserDTO(1l, USERNAME, EMAIL, IMAGE_URL),
						new UserDTO(2l, USERNAME, EMAIL, IMAGE_URL),
						new UserDTO(3l, USERNAME, EMAIL, IMAGE_URL)));
		List<UserDTO> usersResult = service.getByIds(ids);
		
		//Then
		
		//Verify Data Service interaction and capture User entity
		verify(dataService, times(1)).getByIds(ids);
		
		//Verify NO ModelMapper interaction
		verify(mapper, times(0)).map(any(), any());
		
		//Asserts on result
		assertNotNull(usersResult);
		assertFalse(usersResult.isEmpty());
		assertEquals(ids.size(), usersResult.size());
		
		for(int i=0; i<usersResult.size(); i++) {
			
			assertEquals(ids.get(i), usersResult.get(i).getId());
			assertEquals(USERNAME, usersResult.get(i).getUsername());
			assertEquals(EMAIL, usersResult.get(i).getEmail());
			assertEquals(IMAGE_URL, usersResult.get(i).getProfileImageUrl());
		}
	}
	
	@Test
	public void givenUserId_whenDelete_thenCallsMediaServiceDeletePublishKafkaTopicAndReturnsUserDTO() {
		
		//When
		when(dataService.getById( any(Long.class) )).thenReturn(new UserDTO(ID, USERNAME, EMAIL, IMAGE_URL));
		UserDTO userResult = service.delete(ID);
		
		//Then
		
		//Verify Data Service interaction
		verify(dataService, times(1)).getById(any(Long.class));
		
		verify(dataService, times(1)).delete(any(Long.class));
		
		//Verify Media Service interaction
		verify(mediaService, times(1)).delete(IMAGE_URL);
		
		//Verify KafkaTemplate interaction
		verify(kafkaTemplate, times(1)).send("UserDeleted", ID);
		
		//Verify NO ModelMapper interaction
		verify(mapper, times(0)).map(any(), any());
		
		//Asserts on result
		assertNotNull(userResult);
		assertEquals(ID, userResult.getId());
		assertEquals(USERNAME, userResult.getUsername());
		assertEquals(EMAIL, userResult.getEmail());
		assertEquals(IMAGE_URL, userResult.getProfileImageUrl());
	}
	
	@Test
	public void givenInexistentUserId_whenDelete_thenReturnsNull() {
		
		//Given
		Long id = 2l;
		
		//When
		when(dataService.getById( any(Long.class) )).thenReturn(null);
		UserDTO userResult = service.delete(id);
		
		//Then
		
		//Verify Data Service interaction
		verify(dataService, times(1)).getById(any(Long.class));
		
		verify(dataService, times(0)).delete(any());
		
		//Verify NO Media Service interaction
		verify(mediaService, times(0)).delete(any());
		
		//Verify NO KafkaTemplate interaction
		verify(kafkaTemplate, times(0)).send(any(), any());
		
		//Verify NO ModelMapper interaction
		verify(mapper, times(0)).map(any(), any());
		
		//Asserts on result
		assertNull(userResult);
	}
	
	
	
	
	
	private final Long ID = 1l;
	private final String USERNAME = "Username";
	private final String EMAIL = "Email";
	private final String PASSWORD = "PASSWORD";
	private final String IMAGE_URL = "IMAGE_URL";
	private final MultipartFile IMAGE_FILE = mock(MultipartFile.class);
	
	@Mock
	private ModelMapper mapper;
	@Mock
	private MediaService mediaService;
	@Mock
	private KafkaTemplate<String, Object> kafkaTemplate;
	@Mock
	private UserDataService dataService;
	@InjectMocks
	private UserService service;
}
