package com.cesar.User.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;


@Configuration
public class FeignMultipartConfiguration {
    @Bean
    @Primary
    @Scope("prototype")
    Encoder feignFormEncoder() {
        return new SpringFormEncoder();
    }
  }