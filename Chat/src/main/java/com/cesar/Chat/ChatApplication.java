package com.cesar.Chat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class ChatApplication {
	public static void main(String[] args) {
		SpringApplication.run(ChatApplication.class, args);
	}
}