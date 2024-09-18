package com.cesar.ConversationAPI.feign;

import com.cesar.ConversationAPI.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(url="user-api", path="/users.api")
public interface UserFeign {

    @GetMapping("/{id}")
    UserDTO getById(@PathVariable Long id);
}
