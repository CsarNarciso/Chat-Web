package com.cesar.User.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name = "${services.media.name}", 
			url = "${services.media.url}", 
			path = "${services.media.path}",
			fallback = MediaFallback.class)
public interface MediaFeign {
    
	@PostMapping
    String upload(@RequestParam MultipartFile imageMetadata, @RequestParam String oldPath);
    
	@DeleteMapping
    void delete(@RequestParam String path);
}