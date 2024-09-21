package com.cesar.ParticipantAPI.feign;

import com.cesar.ParticipantAPI.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

@FeignClient(url="user-api", path="/users.api")
public interface UserFeign {

    @GetMapping
    Map<Long, UserDTO> getByIds(List<Long> ids);
}