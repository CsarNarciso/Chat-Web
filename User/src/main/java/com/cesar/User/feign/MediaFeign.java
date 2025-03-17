package com.cesar.User.feign;

import com.cesar.User.config.FeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name = "${services.media.name}", 
			url = "${services.media.url}", 
			path = "${services.media.path}",
			fallback = MediaFallback.class,
			configuration = FeignConfiguration.class)
public interface MediaFeign {
    
	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    String upload(@RequestPart MultipartFile imageMetadata);
    
	@DeleteMapping
    void delete(@RequestParam String path);
}