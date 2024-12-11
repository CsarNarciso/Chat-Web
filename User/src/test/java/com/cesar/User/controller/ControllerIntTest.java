package com.cesar.User.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.hamcrest.text.MatchesPattern.matchesPattern;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ControllerIntTest {

	@Test
    public void givenFullCreateRequestButMediaServiceDown_whenCreate_thenCreatesUserWithDefaultImage() throws Exception {
        
		//Arrange
        MockMultipartFile imageFile = 
					new MockMultipartFile("imageMetadata", "customImage.jpg", "image/jpeg", "image".getBytes());

        //Act-Assert
        mvc.perform(multipart("/users")
        		.file(imageFile)
                .param("username", USERNAME)
                .param("email", EMAIL)
                .param("password", PASSWORD)
                .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(1l))
            .andExpect(jsonPath("$.username").value(USERNAME))
            .andExpect(jsonPath("$.email").value(EMAIL))
            .andExpect(jsonPath("$.profileImageUrl").value( matchesPattern(DEFAULT_IMAGE_URL) ));
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
	
	@Autowired
	private MockMvc mvc;
	
	@LocalServerPort
	private int port;
	
	private final String USERNAME = "USERNAME";
	private final String EMAIL = "EMAIL";
	private final String PASSWORD = "PASSWORD";
	
	@Value("defaultImage.name")
	private String defaultProfileImageName;
	private final String DEFAULT_IMAGE_URL = String.format("htpp://localhost:%s/%s", port, defaultProfileImageName);
}