package com.cesar.User.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateRequestDTO {
    private String username;
	private String email;
    private String password;
    private MultipartFile imageMetadata;
}