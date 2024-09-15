package com.cesar.ProfileImageService.feign;

import com.cesar.ProfileImageService.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(name="user-api", path="/users.api")
public interface FeignUser {

    @GetMapping("/v1/{id}")
    UserDTO getById(@PathVariable Long id);

    @PutMapping("/v1/{id}")
    UserDTO update(@PathVariable Long id, UserDTO user);
}