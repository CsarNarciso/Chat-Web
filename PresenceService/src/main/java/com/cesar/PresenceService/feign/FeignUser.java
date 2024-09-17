package com.cesar.PresenceService.feign;

import com.cesar.PresenceService.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(url="user-api", path="/users.api")
public interface FeignUser {

    @GetMapping("/{id}")
    UserDTO getById(@PathVariable Long id);
}