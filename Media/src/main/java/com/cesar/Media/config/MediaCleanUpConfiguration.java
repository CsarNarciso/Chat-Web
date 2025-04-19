package com.cesar.Media.config;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.cesar.Media.MediaApplication;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PreDestroy;

@Component
public class MediaCleanUpConfiguration {
	
	@PreDestroy
	public void cleanUpMediaDirectory() throws URISyntaxException {
		
		Path path = Paths.get(MediaApplication.class.getProtectionDomain()
						.getCodeSource().getLocation()
						.toURI())
				.getParent().getParent().toAbsolutePath().resolve(MEDIA_DIR_NAME);
		
		if(Files.exists(path)) {

			File folder = path.toFile();
			
			File[] files = folder.listFiles();
			
			if(files != null) {
				for(File file : files) {
					if(file.getPath().endsWith(".png") || file.getPath().endsWith(".jpg")){
						file.delete();
					}
				}
			}
		}
	}

	public MediaCleanUpConfiguration(@Value("${media.dirName}") String MEDIA_DIR_NAME) {
		this.MEDIA_DIR_NAME = MEDIA_DIR_NAME;
	}

	private final String MEDIA_DIR_NAME;
}