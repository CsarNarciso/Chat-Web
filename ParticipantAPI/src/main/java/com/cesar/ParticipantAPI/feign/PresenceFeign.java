package com.cesar.ParticipantAPI.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

@FeignClient(url="${services.presence.name}", path="${services.presence.path}")
public interface PresenceFeign {

    @GetMapping
    Map<Long, String> getStatuses(List<Long> ids);
}