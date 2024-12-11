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