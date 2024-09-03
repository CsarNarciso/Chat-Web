package com.cesar.ProfileImageService.config;

import ch.qos.logback.core.model.Model;
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
