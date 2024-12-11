package com.cesar.User.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                title = "USER SERVICE",
                description = "User details data handling and retrieve",
                contact = @Contact(
                        name = "CsarNarciso",
                        url = "https://github.com/CsarNarciso",
                        email = "cesarpazol1029@gmail.com"
                )
        ),
        servers = {
                @Server(
                        description = "DEV",
                        url = "http://localhost:8001"
                )
        }
)
@Configuration
public class SwaggerConfiguration {
	 @Bean
    WebMvcConfigurer corsConfigurer() {
	    return new WebMvcConfigurer() {
	        @Override
	        public void addCorsMappings(CorsRegistry registry) {
	            registry.addMapping("/**")
	                    .allowedOrigins("http://localhost:8000") // Gateway URL
	                    .allowedMethods("*")
	                    .allowedHeaders("*");
	        }
	    };
    }
}