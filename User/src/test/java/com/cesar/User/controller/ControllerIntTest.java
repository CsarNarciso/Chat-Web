package com.cesar.User.controller;


import static org.junit.jupiter.api.Assertions.*;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import com.cesar.User.dto.CreateRequestDTO;
import com.cesar.User.dto.UserDTO;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.maciejwalkowiak.wiremock.spring.EnableWireMock;

@EnableWireMock
public class ControllerIntTest {

    @LocalServerPort
    private int port;
    
    String baseServiceUrl = String.format("http://localhost:%s/users", port);

    @Value("${defaultImage.name}")
    private String defaultProfileImageName;

    private final Long ID = 1L;
    private final String USERNAME = "USERNAME";
    private final String EMAIL = "EMAIL";
    private final String PASSWORD = "PASSWORD";
    private final String CUSTOM_IMAGE_URL = "http://localhost:8003/media/random-image-name.extension"; 
    private final MultipartFile IMAGE_FILE = mock(MultipartFile.class);
    
    private final RestTemplate restTemplate = new RestTemplate();
    
    
    
    @Test
    public void givenFullCreateRequest_whenCreate_thenSaveNewUserInDBAndReturnAsUserDTO() throws Exception {
        
    	WireMockServer wireMockServer = new WireMockServer();
        wireMockServer.start();
    	
        stubFor(post(urlEqualTo("http://localhost:8003/media/upload"))
    				.withRequestBody(isNow())
    			.willReturn(ok("http://localhost:8003/media/random-image-name.extension")));
    	
    	wireMockServer.stop();
    	
    	CreateRequestDTO createRequest = new CreateRequestDTO(USERNAME, EMAIL, PASSWORD, IMAGE_FILE);
    	
    	ResponseEntity<UserDTO> response = restTemplate.postForEntity(baseServiceUrl, createRequest, UserDTO.class);
    	
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(ID, response.getBody().getId());
        assertEquals(USERNAME, response.getBody().getUsername());
        assertEquals(EMAIL, response.getBody().getEmail());
        assertEquals(CUSTOM_IMAGE_URL, response.getBody().getProfileImageUrl());
    }
    
    @Test
    public void givenUserId_whenGetById_thenReturnUserDTO() {
        
    	String url = baseServiceUrl + "/{id}";
    	
    	ResponseEntity<UserDTO> response = restTemplate.exchange(url, HttpMethod.GET, null, UserDTO.class, ID);
    	
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(ID, response.getBody().getId());
        assertEquals(USERNAME, response.getBody().getUsername());
        assertEquals(EMAIL, response.getBody().getEmail());
    }

    public String getDefaultImageUrl() {
        return String.format("http://localhost:%s/%s", port, defaultProfileImageName);
    }
}
