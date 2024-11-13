package com.cesar.Chat.feign;

import java.util.Collections;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.cesar.Chat.dto.UserDTO;

@FeignClient(name = "${services.user.name}",
        url = "${services.user.url}",
        path = "${services.user.path}",
        fallback=UserFeign.UserFeignFallback.class)
public interface UserFeign {

    @GetMapping
    List<UserDTO> getDetails(@RequestBody List<Long> ids);

    @Component
    static class UserFeignFallback implements UserFeign {

        @Override
        public List<UserDTO> getDetails(List<Long> ids) {
            return Collections.emptyList();
        }
    }
}