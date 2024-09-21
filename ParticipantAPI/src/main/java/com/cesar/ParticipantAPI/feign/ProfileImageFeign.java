package com.cesar.ParticipantAPI.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

@FeignClient(url="${services.profileImage.name}" , path="${services.profileImage.path}")
public interface ProfileImageFeign {

    @GetMapping
    Map<Long, byte[]> getByUsersIds(List<Long> usersIds);
}