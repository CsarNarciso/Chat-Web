package com.cesar.User.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import org.hamcrest.text.MatchesPattern;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class ControllerIntTest {

    @Autowired
    private MockMvc mvc;

    @LocalServerPort
    private int port;

    @Value("${defaultImage.name}")
    private String defaultProfileImageName;

    private final String USERNAME = "USERNAME";
    private final String EMAIL = "EMAIL";
    private final String PASSWORD = "PASSWORD";

    @Test
    public void givenUserId_whenGetById_thenReturnUserDTO() throws Exception {
        mvc.perform(get("/users/{id}", 1L))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.username").value(USERNAME))
            .andExpect(jsonPath("$.email").value(EMAIL))
            .andExpect(jsonPath("$.profileImageUrl").value(getDefaultImageUrl()));
    }

    public String getDefaultImageUrl() {
        return String.format("http://localhost:%s/%s", port, defaultProfileImageName);
    }
}
