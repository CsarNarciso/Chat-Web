package com.cesar.Media.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

@Configuration
public class ResourcesConfiguration extends WebMvcConfigurationSupport {
    
	@Value("${media.path}")
	private String mediaPath;
	@Value("${media.dirName}")
	private String mediaDirName;
	
	@Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(String.format("/%s/**", mediaDirName))
                .addResourceLocations(String.format("file:%s/", mediaPath));
    }
}