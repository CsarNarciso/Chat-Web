package com.cesar.Media.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.nio.file.Paths;

@Configuration
public class ResourcesConfiguration extends WebMvcConfigurationSupport {

	@Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(String.format("/%s/**", mediaDirName))
                .addResourceLocations(String.format("file:%s/", mediaPath));
    }


	public ResourcesConfiguration(@Value("${media.dirName}") String mediaDirName) {
		this.mediaDirName = mediaDirName;
		mediaPath = Paths.get("").toAbsolutePath().resolve("Media/" + mediaDirName).toString();
	}

	private final String mediaDirName;
	private final String mediaPath;
}