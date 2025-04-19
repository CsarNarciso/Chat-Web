package com.cesar.Media.config;

import com.cesar.Media.MediaApplication;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.net.URISyntaxException;
import java.nio.file.Paths;

@Configuration
public class ResourcesConfiguration extends WebMvcConfigurationSupport {

	@Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(String.format("/%s/**", mediaDirName))
                .addResourceLocations(String.format("file:%s/", mediaPath));
    }


	public ResourcesConfiguration(@Value("${media.dirName}") String mediaDirName) throws URISyntaxException {
		this.mediaDirName = mediaDirName;
		this.mediaPath = Paths.get(MediaApplication.class.getProtectionDomain()
						.getCodeSource().getLocation()
						.toURI())
				.getParent().getParent().toAbsolutePath().resolve(mediaDirName).toString();
	}

	private final String mediaDirName;
	private final String mediaPath;
}