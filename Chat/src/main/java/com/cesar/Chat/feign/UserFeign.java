package com.cesar.Chat.feign;

import java.util.Collections;
import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.cesar.Chat.dto.UserDTO;
import com.cesar.Chat.feign.UserFeign.UserFeignFallback;

@FeignClient(name = "${services.user.name}",
        url = "${services.user.url}",
        path = "${services.user.path}",
		fallback = UserFeignFallback.class)
public interface UserFeign {

	@GetMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    List<UserDTO> getDetails(@RequestParam List<Long> ids);

    @Component
    static class UserFeignFallback implements UserFeign {

        @Override
        public List<UserDTO> getDetails(List<Long> ids) {
            return Collections.emptyList();
        }
    }
}