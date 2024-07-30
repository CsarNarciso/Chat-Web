package com.cesar.ChatWeb.validation;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;

public class UserValidator {

	@NotBlank(message = "Name required")
	private String name;

	@CustomEmail(message = "Introduce a valid email")
	private String email;

	@NotBlank(message = "Password required")
	private String password;

	private MultipartFile imageMetadata;

	
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public MultipartFile getImageMetadata() {
		return imageMetadata;
	}

	public void setImageMetadata(MultipartFile imageMetadata) {
		this.imageMetadata = imageMetadata;
	}
}