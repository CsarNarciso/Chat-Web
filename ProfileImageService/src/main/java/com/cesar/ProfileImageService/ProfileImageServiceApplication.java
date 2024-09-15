package com.cesar.ProfileImageService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class ProfileImageServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProfileImageServiceApplication.class, args);
	}
}
