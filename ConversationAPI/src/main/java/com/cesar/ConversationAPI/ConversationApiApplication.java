package com.cesar.ConversationAPI;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class ConversationApiApplication {
	public static void main(String[] args) {
		SpringApplication.run(ConversationApiApplication.class, args);
	}
}
