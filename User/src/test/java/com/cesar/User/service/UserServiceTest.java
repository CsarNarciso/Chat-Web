package com.cesar.User.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
import com.cesar.User.helper.ReflectionsHelper;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
	
	@Test
	public void givenFullCreateRequest_whenCreate_thenReturnsUserDTOWithCustomImageUrl() {
		
		//Given
		CreateRequestDTO createRequest = new CreateRequestDTO(USERNAME, EMAIL, PASSWORD, IMAGE_FILE);
		
		when(mapper.map( any(CreateRequestDTO.class), eq(User.class)))
			.thenReturn(new User(null, USERNAME, EMAIL, PASSWORD, null));
		
		when(mediaService.upload( any(MultipartFile.class), eq(null))).thenReturn(IMAGE_URL);
		
		when(dataService.save( any(User.class) )).thenReturn(userDTO);
		
		//When
		UserDTO userResult = service.create(createRequest);
		
		//Then
		
		//Verify Data Service interaction and capture entity before save
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
		
		//Given
		when(dataService.getById( anyLong() )).thenReturn(userDTO);
		
		//When
		UserDTO userResult = service.getById(ID);
		
		//Then
		
		//Verify Data Service interaction
		verify(dataService, times(1)).getById(anyLong());
		
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
		List<Long> ids = List.of(1l, 2l, 4l);
		
		when(dataService.getByIds( ids )).thenReturn(ids
								.stream()
								.map(id -> new UserDTO(id, USERNAME, EMAIL, IMAGE_URL))
								.toList());
		//When
		List<UserDTO> usersResult = service.getByIds(ids);
		
		//Then
		
		//Verify Data Service interaction
		verify(dataService, times(1)).getByIds(ids);
		
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
		
		UserDTO updatedUser = userDTO;
		updatedUser.setUsername(updatedUsername);

	    when(dataService.getById(anyLong())).thenReturn(userDTO);
	    
	    when(mapper.map(any(UserDTO.class), eq(User.class)))
	        .thenReturn(userEntityWithoutPassword);
	    
	    when(dataService.save(any(User.class)))
	        .thenReturn(updatedUser);

	    when(reflectionsHelper.getFieldss(UpdateRequestDTO.class)).thenReturn(UpdateRequestDTO.class.getDeclaredFields());
	    when(reflectionsHelper.findField(eq(User.class), eq("username"))).thenReturn(User.class.getDeclaredField("username"));

	    //When
	    UserDTO userResult = service.updateDetails(ID, updateRequest);

	    //Then
	    
	    //Verify Data Service interactions and capture entity before save
	    verify(dataService, times(1)).getById(anyLong());
	    
	    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
	    verify(dataService, times(1)).save(userCaptor.capture());
	    User entityBeforeSave = userCaptor.getValue();

	    //Verify ModelMapper interactions
	    verify(mapper, times(1)).map(any(UserDTO.class), eq(User.class));
	    
	    //Verify KafkaTemplate interactions
	    verify(kafkaTemplate, times(1)).send(eq("UserUpdated"), any(UserDTO.class));

		//Asserts on entity before save
	    assertNotNull(entityBeforeSave);
	    assertEquals(ID, entityBeforeSave.getId());
	    assertEquals(updatedUsername, entityBeforeSave.getUsername());
	    assertEquals(EMAIL, entityBeforeSave.getEmail());
	    assertEquals(null, entityBeforeSave.getPassword());
	    assertEquals(IMAGE_URL, entityBeforeSave.getProfileImageUrl());

		//Asserts on result
	    assertNotNull(userResult);
	    assertEquals(ID, userResult.getId());
	    assertEquals(updatedUsername, userResult.getUsername());
	    assertEquals(EMAIL, userResult.getEmail());
	    assertEquals(IMAGE_URL, userResult.getProfileImageUrl());
	}


	
	@Test
	public void givenInexistentUserId_whenUpdateDetails_thenReturnsNull() {
		
		//Given
		when(dataService.getById(anyLong())).thenReturn(null);
		
		//When
		UserDTO userResult = service.updateDetails(inexistentUserId, defaultUpdateRequest);
		
		//Then
		
		//Verify Data Service interactions
		verify(dataService, times(1)).getById(anyLong());
		
		verify(dataService, never()).save(any());
		
		//Verify NO ModelMapper interaction
		verify(mapper, never()).map(any(), any());
		
		//Verify NO KafkaTemplate interaction
		verify(kafkaTemplate, never()).send(any(), any());
		
		//Asserts on result
		assertNull(userResult);
	}
	
	@Test
	public void givenUserIdAndNothingToUpdateOnUpdateRequest_whenUpdateDetails_thenReturnsSameUserDTO() {
		
		//Given
		when(dataService.getById( anyLong() )).thenReturn(userDTO);
		
		when(mapper.map( any(UserDTO.class), eq(User.class))).thenReturn(userEntityWithoutPassword);
		
	    when(reflectionsHelper.getFieldss(UpdateRequestDTO.class)).thenReturn( UpdateRequestDTO.class.getDeclaredFields() );
				
		//When
		UserDTO userResult = service.updateDetails(ID, defaultUpdateRequest);
		
		//Then
		
		//Verify Data Service interactions and capture entity before save
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
	public void givenIllegalArgumentOrAccessFieldFromUpdateRequest_whenUpdateDetails_thenReturnsSameUserDTO() throws Exception {
	    
		//Given
	    Field updateRequestField = mock(Field.class);
	    Field[] updateRequestFields = {updateRequestField};

	    when(dataService.getById(anyLong())).thenReturn(userDTO);
	    
		when(mapper.map(any(UserDTO.class), eq(User.class))).thenReturn(userEntityWithoutPassword);
	    
		doThrow(new IllegalArgumentException()).when(updateRequestField).get(any());

	    when(reflectionsHelper.getFieldss(eq(UpdateRequestDTO.class))).thenReturn(updateRequestFields);

	    //When
	    UserDTO userResult = service.updateDetails(ID, defaultUpdateRequest);

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
	public void givenIllegalArgumentOrAccessFieldFromEntity_whenUpdateDetails_thenReturnsSameUserDTO() throws Exception {
	    
		//Given
	    Field entityForUpdateField = mock(Field.class);
	    Field[] entityFields = {entityForUpdateField};

	    when(dataService.getById(anyLong())).thenReturn(userDTO);
	    
		when(mapper.map(any(UserDTO.class), eq(User.class))).thenReturn(userEntityWithoutPassword);
	    
		when(entityForUpdateField.get(any(UpdateRequestDTO.class))).thenReturn(Optional.of(""));
		when(entityForUpdateField.getName()).thenReturn("");
		
		doThrow(new IllegalAccessException()).when(entityForUpdateField).set(any(User.class), any());
		
		when(reflectionsHelper.getFieldss(eq(UpdateRequestDTO.class))).thenReturn(entityFields);
		when(reflectionsHelper.findField(eq(User.class), anyString())).thenReturn(entityForUpdateField);
		
	    //When
	    UserDTO userResult = service.updateDetails(ID, defaultUpdateRequest);

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
	public void givenUserIdAndImageFile_whenUpdateProfileImage_thenReturnsNewImageUrl() {
	    
		//Given
		String newImageUrl = "NewImageUrl";
		
		UserDTO updatedUserDTO = new UserDTO(ID, USERNAME, EMAIL, newImageUrl);;
		
		User updatedUserEntity = userEntityWithoutPassword;
		updatedUserEntity.setProfileImageUrl(newImageUrl);

	    when(dataService.getById(anyLong())).thenReturn(userDTO);
	    
	    when(dataService.save(any(User.class))).thenReturn(updatedUserDTO);
	    
		when(mapper.map(any(UserDTO.class), eq(User.class))).thenReturn(updatedUserEntity);
	    
		when(mediaService.upload(any(MultipartFile.class), eq(IMAGE_URL))).thenReturn(newImageUrl);
		
		//When
		String result = service.updateProfileImage(ID, IMAGE_FILE);
		
		//Then
		
	    //Verify Data Service interaction
		verify(dataService, times(1)).getById(anyLong());
		
		verify(dataService, times(1)).save(any(User.class));
		
		//Verify Media Service interaction
		verify(mediaService, times(1)).upload(any(MultipartFile.class), anyString());
		
		//Verify ModelMapper interaction
		ArgumentCaptor<UserDTO> userCaptor = ArgumentCaptor.forClass(UserDTO.class);
		verify(mapper, times(1)).map(userCaptor.capture(), eq(User.class));
		UserDTO userBeforeMapToEntity = userCaptor.getValue();
		
		//Verify KafkaTemplate interaction
		verify(kafkaTemplate, times(1)).send(eq("UserUpdated"), any(UserDTO.class));
		
		//Asserts on entity before save
		assertNotNull(userBeforeMapToEntity);
		assertEquals(ID, userBeforeMapToEntity.getId());
		assertEquals(USERNAME, userBeforeMapToEntity.getUsername());
		assertEquals(EMAIL, userBeforeMapToEntity.getEmail());
		assertEquals(newImageUrl, userBeforeMapToEntity.getProfileImageUrl());
		
		//Asserts on result
		assertNotNull(result);
		assertEquals(newImageUrl, result);
	}

	
	@Test
	public void givenInexistentUserId_whenUpdateProfileImage_thenReturnsNull() {
	    
		//Given
	    when(dataService.getById(anyLong())).thenReturn(null);
	    
		//When
		String result = service.updateProfileImage(inexistentUserId, IMAGE_FILE);
		
		//Then
		
	    //Verify Data Service interaction
		verify(dataService, times(1)).getById(anyLong());
		
		verify(dataService, never()).save(any());
		
		//Verify NO Media Service interaction
		verify(mediaService, never()).upload(any(), any());
		
		//Verify NO ModelMapper interaction
		verify(mapper, never()).map(any(), any());
		
		//Verify NO KafkaTemplate interaction
		verify(kafkaTemplate, never()).send(any(), any());
		
		//Asserts on result
		assertNull(result);
	}
	
	@Test
	public void givenNewImageUrlIsNull_whenUpdateProfileImage_thenReturnsEmptyString() {
	    
		//Given
	    when(dataService.getById(anyLong())).thenReturn(userDTO);
	    
	    when(mediaService.upload(any(MultipartFile.class), eq(IMAGE_URL))).thenReturn(null);
	    
		//When
		String result = service.updateProfileImage(ID, IMAGE_FILE);
		
		//Then
		
	    //Verify Data Service interaction
		verify(dataService, times(1)).getById(anyLong());
		
		verify(dataService, never()).save(any());
		
		//Verify NO Media Service interaction
		verify(mediaService, times(1)).upload(any(MultipartFile.class), anyString());
		
		//Verify NO ModelMapper interaction
		verify(mapper, never()).map(any(), any());
		
		//Verify NO KafkaTemplate interaction
		verify(kafkaTemplate, never()).send(any(), any());
		
		//Asserts on result
		assertNotNull(result);
		assertEquals("", result);
	}
	
	@Test
	public void givenNewImageUrlEqualsOldOne_whenUpdateProfileImage_thenReturnsEmptyString() {
	    
		//Given
	    when(dataService.getById(anyLong())).thenReturn(userDTO);
	    
	    when(mediaService.upload(any(MultipartFile.class), eq(IMAGE_URL))).thenReturn(IMAGE_URL);
	    
		//When
		String result = service.updateProfileImage(ID, IMAGE_FILE);
		
		//Then
		
	    //Verify Data Service interaction
		verify(dataService, times(1)).getById(anyLong());
		
		verify(dataService, never()).save(any());
		
		//Verify NO Media Service interaction
		verify(mediaService, times(1)).upload(any(MultipartFile.class), anyString());
		
		//Verify NO ModelMapper interaction
		verify(mapper, never()).map(any(), any());
		
		//Verify NO KafkaTemplate interaction
		verify(kafkaTemplate, never()).send(any(), any());
		
		//Asserts on result
		assertNotNull(result);
		assertEquals("", result);
	}
	
	@Test
	public void givenUserId_whenDelete_thenCallsMediaServiceDeletePublishKafkaTopicAndReturnsUserDTO() {
		
		//Given
		when(dataService.getById( anyLong() )).thenReturn(userDTO);
		
		//When
		UserDTO userResult = service.delete(ID);
		
		//Then
		
		//Verify Data Service interaction
		verify(dataService, times(1)).getById(anyLong());
		
		verify(dataService, times(1)).delete(anyLong());
		
		//Verify Media Service interaction
		verify(mediaService, times(1)).delete(IMAGE_URL);
		
		//Verify KafkaTemplate interaction
		verify(kafkaTemplate, times(1)).send(eq("UserDeleted"), anyLong());
		
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
		when(dataService.getById( anyLong() )).thenReturn(null);
		
		//When
		UserDTO userResult = service.delete(inexistentUserId);
		
		//Then
		
		//Verify Data Service interaction
		verify(dataService, times(1)).getById(anyLong());
		
		verify(dataService, never()).delete(any());
		
		//Verify NO Media Service interaction
		verify(mediaService, never()).delete(any());
		
		//Verify NO KafkaTemplate interaction
		verify(kafkaTemplate, never()).send(any(), any());
		
		//Asserts on result
		assertNull(userResult);
	}
	
	
	
	
	
	private final Long ID = 1l;
	private final String USERNAME = "USERNAME";
	private final String EMAIL = "EMAIL";
	private final String PASSWORD = "PASSWORD";
	private final String IMAGE_URL = "IMAGE_URL";
	private final MultipartFile IMAGE_FILE = mock(MultipartFile.class);
	
	private final UserDTO userDTO = new UserDTO(ID, USERNAME, EMAIL, IMAGE_URL);
	private final User userEntityWithoutPassword = new User(ID, USERNAME, EMAIL, null, IMAGE_URL);
	
	private final Long inexistentUserId = 3l;
	
	private final UpdateRequestDTO defaultUpdateRequest = new UpdateRequestDTO();
	
	
	@Mock
	private ModelMapper mapper;
	@Mock
	private MediaService mediaService;
	@Mock
	private KafkaTemplate<String, Object> kafkaTemplate;
	@Mock
	private UserDataService dataService;
	@Mock
	private ReflectionsHelper reflectionsHelper;
	@InjectMocks
	private UserService service;
}
