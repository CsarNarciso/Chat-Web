package com.cesar.Media.config;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PreDestroy;

@Component
public class MediaCleanUpConfiguration {
	
	
	private final String MEDIA_DIR_NAME;

	public MediaCleanUpConfiguration(@Value("${media.dirName}") String MEDIA_DIR_NAME) {
		this.MEDIA_DIR_NAME = MEDIA_DIR_NAME;
	}
	
	@PreDestroy
	public void cleanUpMediaDirectory() {
		
		Path path = Paths.get(MEDIA_DIR_NAME);
		
		if(Files.exists(path)) {

			File folder = path.toFile();
			
			File[] files = folder.listFiles();
			
			if(files != null) {
				for(File file : files) {
					file.delete();
				}
			}
		}
			
	}
	
}