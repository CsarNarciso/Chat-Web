package com.cesar.Chat.feign;

import com.cesar.Chat.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.List;

@FeignClient(name="${services.user.name}", path="${services.user.path}")
public interface UserFeign {
    @GetMapping
    List<UserDTO> getDetails(@RequestBody List<Long> ids);
}