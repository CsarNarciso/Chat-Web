package com.cesar.Message.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GeneralConfiguration {
    @Bean
    ModelMapper modelMapper(){
        return new ModelMapper();
    }
}