package com.cesar.ProfileImageService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ProfileImageServiceApplication {

	public static void main(String[] args) {

		System.out.println("------------DIR-----------********** " + System.getProperty("user.dir"));
		System.out.println("-------------HOME----------********** " + System.getProperty("user.home"));
		SpringApplication.run(ProfileImageServiceApplication.class, args);
	}
}
