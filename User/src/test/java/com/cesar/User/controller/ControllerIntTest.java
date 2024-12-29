package com.cesar.User.controller;


import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.cesar.User.dto.UserDTO;

@AutoConfigureMockMvc
public class ControllerIntTest {

    @LocalServerPort
    private int port;
    
    String serviceUrl = String.format("http://localhost:%s/users", port);

    @Value("${defaultImage.name}")
    private String defaultProfileImageName;

    private final Long ID = 1L;
    private final String USERNAME = "USERNAME";
    private final String EMAIL = "EMAIL";
    private final String PASSWORD = "PASSWORD";

    @Test
    public void givenUserId_whenGetById_thenReturnUserDTO() throws Exception {
        
    	RestTemplate restTemplate = new RestTemplate();
    	
    	String url = serviceUrl + "/{id}";
    	
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
