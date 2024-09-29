package com.cesar.Message.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import java.util.List;

@FeignClient(url="${services.conversation.url}", path="${services.conversation.path}")
public interface ConversationFeign {
    @PostMapping
    Long create(List<Long> participantsIds);
    @PostMapping("/recreate")
    void recreate(Long conversationId, List<Long> recreateFor);
}