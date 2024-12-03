package com.cesar.User.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import com.cesar.User.feign.MediaFeign;

@ExtendWith(MockitoExtension.class)
public class MediaServiceTest {
	
	
	@BeforeEach
	public void init() {
		//Inject default image URL value on Service class field
		ReflectionTestUtils.setField(service, "DEFAULT_IMAGE_URL", DEFAULT_IMAGE_URL);
	}
	
	
	@Test
	public void givenNoArguments_whenUpload_thenReturnsDefaultImageUrl() {
		
		//Given
		ReflectionTestUtils.setField(service, "DEFAULT_IMAGE_URL", DEFAULT_IMAGE_URL);
		
		//When
		String result = service.upload(null, null);
		
		//Then
		
		//Verify NO feign interaction
		verify(feign, never()).upload(any(), any());
		
		//Asserts on result
		assertNotNull(result);
		assertEquals(result, DEFAULT_IMAGE_URL);
	}
	
	@Test
	public void givenImageFileButNoOldPath_whenUpload_thenReturnsNewImageUrl() {
		
		//Given
		when(feign.upload(eq(IMAGE_FILE), eq(null))).thenReturn(NEW_IMAGE_URL);
		
		//When
		String result = service.upload(IMAGE_FILE, null);
		
		//Then
		
		//Verify feign interaction
		verify(feign, times(1)).upload(any(MultipartFile.class), eq(null));
		
		//Asserts on result
		assertNotNull(result);
		assertEquals(result, NEW_IMAGE_URL);
	}
	
	@Test
	public void givenImageFileButNoOldPathAndOnMediaServiceFall_whenUpload_thenReturnsDefaultImageUrl() {
		
		//Given
		ReflectionTestUtils.setField(service, "DEFAULT_IMAGE_URL", DEFAULT_IMAGE_URL);

		when(feign.upload(eq(IMAGE_FILE), eq(null))).thenReturn(null);
		
		//When
		String result = service.upload(IMAGE_FILE, null);
		
		//Then
		
		//Verify feign interaction
		verify(feign, times(1)).upload(any(MultipartFile.class), eq(null));
		
		//Asserts on result
		assertNotNull(result);
		assertEquals(result, DEFAULT_IMAGE_URL);
	}
	
	@Test
	public void givenImageFileAndOldPath_whenUpload_thenReturnsNewImageUrl() {
		
		//Given
		when(feign.upload(eq(IMAGE_FILE), eq(OLD_PATH))).thenReturn(NEW_IMAGE_URL);
		
		//When
		String result = service.upload(IMAGE_FILE, OLD_PATH);
		
		//Then
		
		//Verify feign interaction
		verify(feign, times(1)).upload(any(MultipartFile.class), anyString());
		
		//Asserts on result
		assertNotNull(result);
		assertEquals(result, NEW_IMAGE_URL);
	}
	
	@Test
	public void givenOldPathButNoImageFile_whenUpload_thenReturnsNull() {
		
		//When
		String result = service.upload(null, OLD_PATH);
		
		//Then
		
		//Verify NO feign interaction
		verify(feign, never()).upload(any(), any());
		
		//Asserts on result
		assertNull(result);
	}
	
	@Test
	public void givenOldPathButEmptyImageFile_whenUpload_thenReturnsNull() {
		
		//Given
		when(IMAGE_FILE.isEmpty()).thenReturn(true);
		
		//When
		String result = service.upload(IMAGE_FILE, OLD_PATH);
		
		//Then
		
		//Verify NO feign interaction
		verify(feign, never()).upload(any(), any());
		
		//Asserts on result
		assertNull(result);
	}
	
	@Test
	public void givenCustomImagePath_whenDelete_thenCallsFeignDelete() {
		
		//When
		service.delete(OLD_PATH);
		
		//Then
		
		//Verify feign interaction
		verify(feign, times(1)).delete(eq(OLD_PATH));
	}
	
	@Test
	public void givenDefaultImagePath_whenDelete_thenDontCallFeignDelete() {
		
		//When
		service.delete(DEFAULT_IMAGE_URL);
		
		//Then
		
		//Verify feign interaction
		verify(feign, never()).delete(any());
	}
	
	
	
	
	
	
	
	
	private final MultipartFile IMAGE_FILE = mock(MultipartFile.class);
	private final String OLD_PATH = "OLD_PATH";
	private final String NEW_IMAGE_URL = "NEW_IMAGE_URL";
	private final String DEFAULT_IMAGE_URL = "DEFAULT_IMAGE_URL";
	
	
	@Mock 
	private MediaFeign feign;
	
	@InjectMocks
	private MediaService service;
}