package com.cesar.User.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import com.cesar.User.feign.MediaFeign;

@ExtendWith(MockitoExtension.class)
public class MediaServiceTest {

	@Test
	public void givenImageFileAndOldPath_whenUpload_thenReturnsNewImageUrl() {
		
		//Given
		when(feign.upload(eq(IMAGE_FILE), eq(OLD_PATH))).thenReturn(NEW_IMAGE_URL);
		
		//When
		String result = service.upload(IMAGE_FILE, OLD_PATH);
		
		//Then
		assertNotNull(result);
		assertEquals(result, NEW_IMAGE_URL);
	}
	
	
	
	
	
	
	private final MultipartFile IMAGE_FILE = mock(MultipartFile.class);
	private final String OLD_PATH = "OLD_PATH";
	private final String NEW_IMAGE_URL = "NEW_IMAGE_URL";
	
	
	@Mock 
	private MediaFeign feign;
	
	@InjectMocks
	private MediaService service;
}