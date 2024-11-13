package com.cesar.Chat.feign;

import com.cesar.Chat.dto.UserPresenceDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.Collections;
import java.util.List;

@FeignClient(
        name="${services.presence.name}",
        url="${services.presence.url}",
        path="${services.presence.path}")
public interface PresenceFeign {

    @GetMapping
    List<UserPresenceDTO> getByUserIds(@RequestBody List<Long> userIds);


    @Component
    class Fallback implements PresenceFeign {

        @Override
        public List<UserPresenceDTO> getByUserIds(List<Long> userIds) {
            return Collections.emptyList();
        }
    }
}