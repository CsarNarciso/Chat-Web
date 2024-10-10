package com.cesar.User.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(url="${services.media.url}", path="${services.media.path}")
public interface MediaFeign {
    @PostMapping
    String upload(MultipartFile imageMetadata, String oldPath);
    @DeleteMapping
    void delete(String path);
}