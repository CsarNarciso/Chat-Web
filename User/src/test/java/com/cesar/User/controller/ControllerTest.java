package com.cesar.User.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
public class ControllerTest {

	@Test
	public void givenFullCreateRequest_whenCreate_thenReturnsUserDTOWithCustomImageUrl() {
		
		//Given
		CreateRequestDTO createRequest = new CreateRequestDTO(USERNAME, EMAIL, PASSWORD, IMAGE_FILE);
		
		when(service.create( any(CreateRequestDTO.class) )).thenReturn(userDTO);
		
		//When
		ResponseEntity<UserDTO> result = controller.create(createRequest);
		
		//Then
		
		//Verify Service interaction
		verify(service, times(1)).create( any(CreateRequestDTO.class) );
		
		//Asserts on result
		assertNotNull(result);
		assertEquals(userDTO, result.getBody());
	}
	
	
	
	private final Long ID = 1l;
	private final String USERNAME = "USERNAME";
	private final String EMAIL = "EMAIL";
	private final String PASSWORD = "PASSWORD";
	private final String IMAGE_URL = "IMAGE_URL";
	private final MultipartFile IMAGE_FILE = mock(MultipartFile.class);
	
	private final UserDTO userDTO = new UserDTO(ID, USERNAME, EMAIL, IMAGE_URL);
	private final User userEntityWithoutPassword = new User(ID, USERNAME, EMAIL, null, IMAGE_URL);
	
	private final Long inexistentUserId = 99l;
	
	private final CreateRequestDTO createRequest = new CreateRequestDTO();
	private final UpdateRequestDTO defaultUpdateRequest = new UpdateRequestDTO();
	
	
	@Mock
	private UserService service;
	
	@InjectMocks
	private Controller controller;
}
