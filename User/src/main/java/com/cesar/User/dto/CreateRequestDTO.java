package com.cesar.User.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class CreateRequestDTO {
    private String username;
    private String email;
    private String password;
    private MultipartFile profileImageMetadata;
}