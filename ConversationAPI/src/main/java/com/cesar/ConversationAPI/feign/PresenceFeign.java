package com.cesar.ConversationAPI.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(url="${services.presence.name}", path="${services.presence.path}")
public interface PresenceFeign {

    @GetMapping("/{userId}")
    String getUserStatus(@PathVariable Long userId);
}