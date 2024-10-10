package com.cesar.User.feign;

import com.cesar.User.dto.RelationshipDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(url="${services.social.url}", path="${services.social.path}")
public interface SocialFeign {
    @GetMapping("/{userId}")
    RelationshipDTO getUserRelationships(@PathVariable Long userId);

}