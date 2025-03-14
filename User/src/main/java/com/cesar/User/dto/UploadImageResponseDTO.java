package com.cesar.User.dto;

import org.springframework.http.HttpStatusCode;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UploadImageResponseDTO {
	
	private String message;
	private HttpStatusCode httpStatusCode;
	private String imageUrl;
}