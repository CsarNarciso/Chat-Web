package com.cesar.User.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

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
import com.cesar.User.dto.UpdateRequestDTO;
import com.cesar.User.dto.UserDTO;
import com.cesar.User.entity.User;
import com.cesar.User.helper.ReflectionHelper;

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
		assertEquals(PASSWORD, entityBeforeSave.getPassword());
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
	public void givenUserIdAndUpdateRequest_whenUpdateDetails_thenPublishKafkaTopicAndReturnsUpdatedUserDTO() throws Exception {
	    
		// Given
	    String updatedUsername = "UpdatedUsername";
	    UpdateRequestDTO updateRequest = new UpdateRequestDTO(Optional.of(updatedUsername), Optional.empty());

	    when(dataService.getById(any(Long.class))).thenReturn(new UserDTO(ID, USERNAME, EMAIL, IMAGE_URL));
	    
	    when(mapper.map(any(UserDTO.class), eq(User.class)))
	        .thenReturn(new User(ID, USERNAME, EMAIL, null, IMAGE_URL));
	    
	    when(dataService.save(any(User.class)))
	        .thenReturn(new UserDTO(ID, updatedUsername, EMAIL, IMAGE_URL));

	    when(rh.getFieldss(UpdateRequestDTO.class)).thenReturn(UpdateRequestDTO.class.getDeclaredFields());
	    when(rh.findField(eq(User.class), eq("username"))).thenReturn(User.class.getDeclaredField("username"));

	    //Then-When
	    UserDTO userResult = service.updateDetails(ID, updateRequest);

	    //Then
	    
	    //Verify Data Service interactions and capture entity before save
	    verify(dataService, times(1)).getById(any(Long.class));
	    
	    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
	    verify(dataService, times(1)).save(userCaptor.capture());
	    User entityBeforeSave = userCaptor.getValue();

	    //Verify ModelMapper interactions
	    verify(mapper, times(1)).map(any(UserDTO.class), eq(User.class));
	    
	    //Verify KafkaTemplate interactions
	    verify(kafkaTemplate, times(1)).send(eq("UserUpdated"), any(UserDTO.class));

	    assertNotNull(entityBeforeSave);
	    assertEquals(ID, entityBeforeSave.getId());
	    assertEquals(updatedUsername, entityBeforeSave.getUsername());
	    assertEquals(EMAIL, entityBeforeSave.getEmail());
	    assertEquals(null, entityBeforeSave.getPassword());
	    assertEquals(IMAGE_URL, entityBeforeSave.getProfileImageUrl());

	    assertNotNull(userResult);
	    assertEquals(ID, userResult.getId());
	    assertEquals(updatedUsername, userResult.getUsername());
	    assertEquals(EMAIL, userResult.getEmail());
	    assertEquals(IMAGE_URL, userResult.getProfileImageUrl());
	}


	
	@Test
	public void givenInexistentUserId_whenUpdateDetails_thenReturnsNull() {
		
		//Given
		Long id = 2l;
		UpdateRequestDTO updateRequest = new UpdateRequestDTO();
		
		//When
		when(dataService.getById( any(Long.class) )).thenReturn(null);
		
		UserDTO userResult = service.updateDetails(id, updateRequest);
		
		//Then
		
		//Verify Data Service interactions
		verify(dataService, times(1)).getById(any(Long.class));
		
		verify(dataService, times(0)).save(any());
		
		//Verify NO ModelMapper interaction
		verify(mapper, times(0)).map(any(), any());
		
		//Verify NO KafkaTemplate interaction
		verify(kafkaTemplate, times(0)).send(any(), any());
		
		//Asserts on result
		assertNull(userResult);
	}
	
	@Test
	public void givenUserIdAndNothingToUpdateOnUpdateRequest_whenUpdateDetails_thenReturnsSameUserDTO() {
		
		//Given
		UpdateRequestDTO updateRequest = new UpdateRequestDTO(
												Optional.empty(), 
												Optional.empty());
		
		//When
		when(dataService.getById( any(Long.class) )).thenReturn(new UserDTO(ID, USERNAME, EMAIL, IMAGE_URL));
		
		when(mapper.map( any(UserDTO.class), eq(User.class)))
			.thenReturn(new User(ID, USERNAME, EMAIL, PASSWORD, IMAGE_URL));
		
	    when(rh.getFieldss(UpdateRequestDTO.class)).thenReturn( UpdateRequestDTO.class.getDeclaredFields() );
				
		UserDTO userResult = service.updateDetails(ID, updateRequest);
		
		//Then
		
		//Verify Data Service interactions and capture entity before save
		verify(dataService, times(1)).getById(any(Long.class));
		
		verify(dataService, times(0)).save(any());
		
		//Verify ModelMapper interaction
		verify(mapper, times(1)).map(any(UserDTO.class), eq(User.class));
		
		//Verify NO KafkaTemplate interaction
		verify(kafkaTemplate, times(0)).send(any(), any());
		
		//Asserts on result
		assertNotNull(userResult);
		assertEquals(ID, userResult.getId());
		assertEquals(USERNAME, userResult.getUsername());
		assertEquals(EMAIL, userResult.getEmail());
		assertEquals(IMAGE_URL, userResult.getProfileImageUrl());
	}
	
	@Test
	public void givenIllegalArgumentOrAccessFieldFromUpdateRequest_whenUpdateDetails_thenReturnsSameUserDTO() throws Exception {
	    
		//Given
	    UpdateRequestDTO updateRequest = new UpdateRequestDTO();
	    Field updateRequestField = mock(Field.class);
	    Field[] updateRequestFields = {updateRequestField};

	    //When
	    when(dataService.getById(any(Long.class))).thenReturn(new UserDTO(ID, USERNAME, EMAIL, IMAGE_URL));
	    
		when(mapper.map(any(UserDTO.class), eq(User.class))).thenReturn(new User(ID, USERNAME, EMAIL, null, IMAGE_URL));
	    
		doThrow(new IllegalArgumentException()).when(updateRequestField).get(any());

	    when(rh.getFieldss(eq(UpdateRequestDTO.class))).thenReturn(updateRequestFields);

	    UserDTO userResult = service.updateDetails(ID, updateRequest);

	    //Then
		
	    //Verify Data Service interaction
		verify(dataService, times(1)).getById(any(Long.class));
		
		verify(dataService, never()).save(any());
		
		//Verify ModelMapper interaction
		verify(mapper, times(1)).map(any(UserDTO.class), eq(User.class));
		
		//Verify NO KafkaTemplate interaction
		verify(kafkaTemplate, never()).send(any(), any());
		
		//Asserts on result
		assertNotNull(userResult);
		assertEquals(ID, userResult.getId());
		assertEquals(USERNAME, userResult.getUsername());
		assertEquals(EMAIL, userResult.getEmail());
		assertEquals(IMAGE_URL, userResult.getProfileImageUrl());
	}
	
	@Test
	public void givenIllegalArgumentOrAccessFieldFromEntity_whenUpdateDetails_thenReturnsSameUserDTO() throws Exception {
	    
		//Given
	    UpdateRequestDTO updateRequest = new UpdateRequestDTO();
	    Field entityForUpdateField = mock(Field.class);
	    Field[] entityFields = {entityForUpdateField};

	    //When
	    when(dataService.getById(any(Long.class))).thenReturn(new UserDTO(ID, USERNAME, EMAIL, IMAGE_URL));
	    
		when(mapper.map(any(UserDTO.class), eq(User.class))).thenReturn(new User(ID, USERNAME, EMAIL, null, IMAGE_URL));
	    
		when(entityForUpdateField.get(any(UpdateRequestDTO.class))).thenReturn(Optional.of(""));
		when(entityForUpdateField.getName()).thenReturn("");
		
		doThrow(new IllegalAccessException()).when(entityForUpdateField).set(any(User.class), any());
		
		when(rh.getFieldss(eq(UpdateRequestDTO.class))).thenReturn(entityFields);
		when(rh.findField(eq(User.class), anyString())).thenReturn(entityForUpdateField);
		
	    UserDTO userResult = service.updateDetails(ID, updateRequest);

	    //Then
		
	    //Verify Data Service interaction
		verify(dataService, times(1)).getById(anyLong());
		
		verify(dataService, never()).save(any());
		
		//Verify ModelMapper interaction
		verify(mapper, times(1)).map(any(UserDTO.class), eq(User.class));
		
		//Verify NO KafkaTemplate interaction
		verify(kafkaTemplate, never()).send(any(), any());
		
		//Asserts on result
		assertNotNull(userResult);
		assertEquals(ID, userResult.getId());
		assertEquals(USERNAME, userResult.getUsername());
		assertEquals(EMAIL, userResult.getEmail());
		assertEquals(IMAGE_URL, userResult.getProfileImageUrl());
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
	@Mock
	private ReflectionHelper rh;
	@InjectMocks
	private UserService service;
}
