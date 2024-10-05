package com.cesar.Chat.feign;

import com.cesar.Chat.dto.PresenceStatusDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

@FeignClient(url="${services.presence.url}", path="${services.presence.path}")
public interface PresenceFeign {
    @GetMapping
    List<PresenceStatusDTO> getStatuses(List<Long> usersIds);
}