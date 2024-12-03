package com.cesar.User.feign;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
public class MediaFallbackTest {
	
	@Test
	public void givenImageFileAndOldPath_whenUpload_thenReturnsOldPath() {
		
		//When
		String result = fallback.upload(IMAGE_FILE, OLD_PATH);
		
		//Then
		
		//Asserts on result
		assertNotNull(result);
		assertEquals(result, OLD_PATH);
	}
	
	@Test
	public void givenImagePath_whenDelete_thenDontDoNothing() {
		fallback.delete(OLD_PATH);
	}
	
	
	
	private final MultipartFile IMAGE_FILE = mock(MultipartFile.class);
	private final String OLD_PATH = "OLD_PATH";
	
	private final MediaFallback fallback = new MediaFallback();
}