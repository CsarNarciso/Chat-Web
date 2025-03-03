package com.cesar.User.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import com.cesar.User.dto.CreateRequestDTO;
import com.cesar.User.dto.UpdateRequestDTO;
import com.cesar.User.dto.UserDTO;
import com.cesar.User.service.UserService;

@ExtendWith(MockitoExtension.class)
public class ControllerTest {

	@Test
	public void givenFullCreateRequest_whenCreate_thenReturnsUserDTOWithCustomImageUrl() {
		
		//Given
		CreateRequestDTO createRequest = new CreateRequestDTO(USERNAME, EMAIL, PASSWORD, IMAGE_FILE);
		
		when(service.create( any(CreateRequestDTO.class) )).thenReturn(userDTO);
		
		//When
		ResponseEntity<?> result = controller.create(createRequest);
		
		//Then
		
		//Verify Service interaction
		verify(service, times(1)).create( any(CreateRequestDTO.class) );
		
		//Asserts on result
		assertNotNull(result);
		assertTrue(result.getBody() instanceof UserDTO);
		assertEquals(userDTO, result.getBody());
		assertEquals(HttpStatus.CREATED, result.getStatusCode());
	}
	
	@Test
	public void givenUserId_whenGetById_thenReturnsUserDTO() {
		
		//Given
		when(service.getById( anyLong() )).thenReturn(userDTO);
		
		//When
		ResponseEntity<?> result = controller.getById(ID);
		
		//Then
		
		//Verify Service interaction
		verify(service, times(1)).getById(anyLong());
		
		//Asserts on result
		assertNotNull(result);
		assertTrue(result.getBody() instanceof UserDTO);
		assertEquals(userDTO, result.getBody());
		assertEquals(HttpStatus.OK, result.getStatusCode());
	}
	
	@Test
	public void givenInexistentUserId_whenGetById_thenReturnsNull() {
		
		//Given
		when(service.getById( anyLong() )).thenReturn(null);
		
		//When
		ResponseEntity<?> result = controller.getById(inexistentUserId);
		
		//Then
		
		//Verify Service interaction
		verify(service, times(1)).getById(anyLong());
		
		//Asserts on result
		assertNotNull(result);
		assertEquals(null, result.getBody());
		assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
	}
	
	@Test
	public void givenUserIds_whenGetByIds_thenReturnsListOfUserDTOs() {
		
		//Given
		List<Long> ids = List.of(1l, 2l, 4l);
		
		when(service.getByIds( ids )).thenReturn(ids
								.stream()
								.map(id -> new UserDTO(id, USERNAME, EMAIL, IMAGE_URL))
								.toList());
		//When
		ResponseEntity<?> result = controller.getByIds(ids);
		
		//Then
		
		//Verify Service interaction
		verify(service, times(1)).getByIds(ids);
		
		//Asserts on result
		assertNotNull(result);
		assertEquals(HttpStatus.OK, result.getStatusCode());
		
		List<UserDTO> usersResultList = castList(result.getBody(), UserDTO.class);
		assertFalse(usersResultList.isEmpty());
		assertEquals(usersResultList.size(), usersResultList.size());
		
		for(int i=0; i<usersResultList.size(); i++) {
			
			assertEquals(ids.get(i), usersResultList.get(i).getId());
			assertEquals(USERNAME, usersResultList.get(i).getUsername());
			assertEquals(EMAIL, usersResultList.get(i).getEmail());
			assertEquals(IMAGE_URL, usersResultList.get(i).getProfileImageUrl());
		}
	}
	
	@Test
	public void givenInexistentUserIds_whenGetByIds_thenReturnsNull() {
		
		//Given
		List<Long> ids = List.of(1l, 2l, 4l);
		
		when(service.getByIds( ids )).thenReturn(null);
		
		//When
		ResponseEntity<?> result = controller.getByIds(ids);
		
		//Then
		
		//Verify Service interaction
		verify(service, times(1)).getByIds(ids);
		
		//Asserts on result
		assertNotNull(result);
		assertNull(result.getBody());
		assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
	}
	
	@Test
	public void givenUserIdAndUpdateRequest_whenUpdateDetails_thenReturnsUpdatedUserDTO() {
	    
		// Given
	    String updatedUsername = "UpdatedUsername";
	    UpdateRequestDTO updateRequest = new UpdateRequestDTO(Optional.of(updatedUsername), Optional.empty());
		
		UserDTO updatedUser = new UserDTO(ID, updatedUsername, EMAIL, IMAGE_URL);

	    when(service.updateDetails( anyLong(), any(UpdateRequestDTO.class) )).thenReturn(updatedUser);
	    
	    //When
	    ResponseEntity<?> result = controller.updateDetails(ID, updateRequest);

	    //Then
	    
	    //Verify Service interactions
	    verify(service, times(1)).updateDetails( anyLong(), any(UpdateRequestDTO.class) );
	    
		//Asserts on result
	    assertNotNull(result);
	    assertEquals(HttpStatus.OK, result.getStatusCode());
		
		UserDTO userResult = (UserDTO) result.getBody();
	    assertEquals(ID, userResult.getId());
	    assertEquals(updatedUsername, userResult.getUsername());
	    assertEquals(EMAIL, userResult.getEmail());
	    assertEquals(IMAGE_URL, userResult.getProfileImageUrl());
	}
	
	@Test
	public void givenInexistentUserId_whenUpdateDetails_thenReturnsNull() {
	    
		// Given
	    when(service.updateDetails( anyLong(), any(UpdateRequestDTO.class) )).thenReturn(null);
	    
	    //When
	    ResponseEntity<?> result = controller.updateDetails(inexistentUserId, new UpdateRequestDTO());

	    //Then
	    
	    //Verify Service interactions
	    verify(service, times(1)).updateDetails( anyLong(), any(UpdateRequestDTO.class) );
	    
		//Asserts on result
	    assertNotNull(result);
		assertNull(result.getBody());
		assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
	}

	@Test
	public void givenUserIdAndImageFile_whenUpdateProfileImage_thenReturnsNewImageUrl() {
	    
		//Given
		String newImageUrl = "NewImageUrl";
		
		when(service.updateProfileImage(anyLong(), any(MultipartFile.class))).thenReturn(newImageUrl);
		
		//When
		ResponseEntity<?> result = controller.updateProfileImage(ID, IMAGE_FILE);
		
		//Then
		
	    //Verify Data Service interaction
		verify(service, times(1)).updateProfileImage(anyLong(), any(MultipartFile.class));
		
		//Asserts on result
		assertNotNull(result);
		assertEquals(newImageUrl, result.getBody());
		assertEquals(HttpStatus.OK, result.getStatusCode());
	}
	
	@Test
	public void givenInexistentUserId_whenUpdateProfileImage_thenReturnsNull() {
	    
		//Given
		when(service.updateProfileImage(anyLong(), any(MultipartFile.class))).thenReturn(null);
		
		//When
		ResponseEntity<?> result = controller.updateProfileImage(inexistentUserId, IMAGE_FILE);
		
		//Then
		
	    //Verify Data Service interaction
		verify(service, times(1)).updateProfileImage(anyLong(), any(MultipartFile.class));
		
		//Asserts on result
		assertNotNull(result);
		assertNull(result.getBody());
		assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
	}
	
	@Test
	public void givenNewImageUrlIsEmpty_whenUpdateProfileImage_thenReturnsUnavailableServiceMessage() {
	    
		//Given
		when(service.updateProfileImage(anyLong(), any(MultipartFile.class))).thenReturn("");
		
		//When
		ResponseEntity<?> result = controller.updateProfileImage(ID, IMAGE_FILE);
		
		//Then
		
	    //Verify Data Service interaction
		verify(service, times(1)).updateProfileImage(anyLong(), any(MultipartFile.class));
		
		//Asserts on result
		assertNotNull(result);
		assertEquals("Media service down or unreachable", result.getBody());
		assertEquals(HttpStatus.SERVICE_UNAVAILABLE, result.getStatusCode());
	}

	@Test
	public void givenUserId_whenDelete_thenReturnsNO_CONTENT() {
	    
		//Given
		when(service.delete( anyLong() )).thenReturn(userDTO);
		
		//When
		ResponseEntity<?> result = controller.delete(ID);
		
		//Then
		
	    //Verify Data Service interaction
		verify(service, times(1)).delete( anyLong() );
		
		//Asserts on result
		assertNotNull(result);
		assertNull(result.getBody());
		assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
	}

	@Test
	public void givenInexistentUserId_whenDelete_thenReturnsNOT_FOUND() {
	    
		//Given
		when(service.delete( anyLong() )).thenReturn(null);
		
		//When
		ResponseEntity<?> result = controller.delete(ID);
		
		//Then
		
	    //Verify Data Service interaction
		verify(service, times(1)).delete( anyLong() );
		
		//Asserts on result
		assertNotNull(result);
		assertNull(result.getBody());
		assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
	}

	
	
	
	
	private static <T> List<T> castList(Object obj, Class<T> clazz)
	{
	    List<T> result = new ArrayList<T>();
	    if(obj instanceof List<?>)
	    {
	        for (Object o : (List<?>) obj)
	        {
	            result.add(clazz.cast(o));
	        }
	        return result;
	    }
	    return null;
	}
	
	private final Long ID = 1l;
	private final Long inexistentUserId = 99l;
	private final String USERNAME = "USERNAME";
	private final String EMAIL = "EMAIL";
	private final String PASSWORD = "PASSWORD";
	private final String IMAGE_URL = "IMAGE_URL";
	private final MultipartFile IMAGE_FILE = mock(MultipartFile.class);
	private final UserDTO userDTO = new UserDTO(ID, USERNAME, EMAIL, IMAGE_URL);
	
	@Mock
	private UserService service;
	@InjectMocks
	private Controller controller;
}
