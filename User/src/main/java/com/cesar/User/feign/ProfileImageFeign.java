package com.cesar.User.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(url="${services.profileImage.url}", path="${services.profileImage.path}")
public interface ProfileImageFeign {
    @PostMapping
    String upload(Long userId, MultipartFile imageMetadata);
}