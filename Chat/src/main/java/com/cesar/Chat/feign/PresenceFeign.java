package com.cesar.Chat.feign;

import java.util.Collections;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.cesar.Chat.dto.PresenceDTO;

@FeignClient(
        name="${services.presence.name}",
        url="${services.presence.url}",
        path="${services.presence.path}",
        fallback=PresenceFeign.Fallback.class)
public interface PresenceFeign {

    @GetMapping
    List<PresenceDTO> getByUserIds(@RequestBody List<Long> userIds);


    @Component
    class Fallback implements PresenceFeign {

        @Override
        public List<PresenceDTO> getByUserIds(List<Long> userIds) {
            return Collections.emptyList();
        }
    }
}