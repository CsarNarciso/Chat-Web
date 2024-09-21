package com.cesar.ConversationAPI.feign;

import com.cesar.ConversationAPI.dto.ParticipantDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@FeignClient(url="${services.participant.name}", path="${services.participant.path}")
public interface ParticipantFeign {

    @PostMapping
    List<ParticipantDTO> buildParticipants(List<Long> participantsIds);
}