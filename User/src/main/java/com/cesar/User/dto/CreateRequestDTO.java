package com.cesar.User.dto;

import org.springframework.web.multipart.MultipartFile;
import lombok.Data;

@Data
public class CreateRequestDTO {
    private String username;
    private String email;
    private String password;
    private MultipartFile imageMetadata;
}