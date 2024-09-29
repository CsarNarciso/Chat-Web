package com.cesar.Message;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class MessageApplication {
	public static void main(String[] args) {
		SpringApplication.run(MessageApplication.class, args);
	}
}