package com.cesar.User.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class ControllerIntTest {

	@Test
    public void givenValidCreateRequest_whenCreate_thenReturnsUserDTOWithCustomImageUrl() throws Exception {
        
		// Arrange
        MockMultipartFile imageFile = 
					new MockMultipartFile("imageMetadata", "customImage.jpg", "image/jpeg", "image".getBytes());
        
        MockMultipartFile username = new MockMultipartFile("username", "", MediaType.TEXT_PLAIN, USERNAME.getBytes());
        MockMultipartFile email = new MockMultipartFile("email", "", MediaType.TEXT_PLAIN, EMAIL.getBytes());
        MockMultipartFile password = new MockMultipartFile("password", "", MediaType.TEXT_PLAIN, PASSWORD.getBytes());

        // Act-Assert
        mvc.perform(multipart("/users")
                .file(imageFile)
                .file(username)
                .file(email)
                .file(password)
                .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(ID))
            .andExpect(jsonPath("$.username").value(USERNAME))
            .andExpect(jsonPath("$.email").value(EMAIL))
            .andExpect(jsonPath("$.profileImageUrl").value(customProfileImageBaseUrl + UUID.getRandom() + ".jpg"));
    }

	@Test
    public void givenUserId_whenGetById_thenReturnUserDTO() throws Exception {
        
        // Act-Assert
        mvc.perform(get("/users/{id}", 1l)
            .andExpect(status().isOK())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(1l))
            .andExpect(jsonPath("$.username").value(USERNAME))
            .andExpect(jsonPath("$.email").value(EMAIL))
            .andExpect(jsonPath("$.profileImageUrl").value("http://localhost:8001/DefaultProfileImage.png"));
    }
	
	@Test
    public void givenInexistentUserId_whenGetById_thenReturnNull() throws Exception {
        
        // Act-Assert
        mvc.perform(get("/users/{id}", 2l)
            .andExpect(status().isNotFound())
    }
	
	@Test
    public void givenUserIds_whenGetByIds_thenReturnListOfUserDTO() throws Exception {
        
        // Act-Assert
        mvc.perform(get("/users")
				.param("ids", 2l, 1l)
            .andExpect(status().isOK())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].id").value(2l))
            .andExpect(jsonPath("$[0].username").value(USERNAME))
            .andExpect(jsonPath("$[0].email").value(EMAIL))
            .andExpect(jsonPath("$[0].profileImageUrl").value("http://localhost:8001/DefaultProfileImage.png"))
			.andExpect(jsonPath("$[1].id").value(1l))
            .andExpect(jsonPath("$[1].username").value(USERNAME))
            .andExpect(jsonPath("$[1].email").value(EMAIL))
            .andExpect(jsonPath("$[1].profileImageUrl").value("http://localhost:8001/DefaultProfileImage.png"));
    }
	
	@Test
    public void givenInexistentUserIds_whenGetById_thenReturnNull() throws Exception {
        
        // Act-Assert
        mvc.perform(get("/users", 3l, 4l)
            .andExpect(status().isNotFound())
    }
	
	private final String USERNAME = "USERNAME";
	private final String EMAIL = "EMAIL";
	private final String PASSWORD = "PASSWORD";

	@Value("defaultImage.url")
	private final String defaultProfileImageUrl;
	
	@Value("services.media.url")
	private final String mediaServiceUrl;
	private final String customProfileImageBaseUrl = mediaServiceUrl + "/media";
	
	@Autowired
    private ObjectMapper objectMapper;
	
	@Autowired
	private MockMvc mvc;
}