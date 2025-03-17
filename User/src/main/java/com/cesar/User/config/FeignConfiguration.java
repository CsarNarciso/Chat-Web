package com.cesar.User.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;

import com.cesar.User.feign.MediaFeignErrorDecoder;

import feign.codec.Encoder;
import feign.codec.ErrorDecoder;
import feign.form.spring.SpringFormEncoder;

@Configuration
public class FeignConfiguration {
	
	//Multipart configuration to handle form data type (files)
	@Bean
	@Primary
	@Scope("prototype")
	Encoder feignFormEncoder() {
		return new SpringFormEncoder();
	}
	
	//Custom error decoder
	@Bean
    public ErrorDecoder errorDecoder() {
        return new MediaFeignErrorDecoder();
    }
}