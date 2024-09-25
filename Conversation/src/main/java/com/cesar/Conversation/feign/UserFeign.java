package com.cesar.Conversation.feign;

import com.cesar.Conversation.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(url="${services.user.url}", path="${services.user.path}")
public interface UserFeign {
    @GetMapping
    List<UserDTO> getDetails(List<Long> ids);
}