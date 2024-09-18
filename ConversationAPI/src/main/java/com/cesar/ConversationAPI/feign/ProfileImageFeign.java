package com.cesar.ConversationAPI.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(url="${services.profileImage.name}" , path="${services.profileImage.path}")
public interface ProfileImageFeign {

    @GetMapping("/{path}")
    byte[] getByPath(@PathVariable String path);
}
