package com.cesar.ParticipantAPI.service;

import com.cesar.ParticipantAPI.dto.ParticipantDTO;
import com.cesar.ParticipantAPI.dto.UserDTO;
import com.cesar.ParticipantAPI.feign.PresenceFeign;
import com.cesar.ParticipantAPI.feign.ProfileImageFeign;
import com.cesar.ParticipantAPI.feign.UserFeign;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ParticipantService {

    public List<ParticipantDTO> buildParticipants(Long senderId, List<Long> participantsIds){

        List<ParticipantDTO> participants = new ArrayList<>();

        //Fetch all participants data
        Map<Long, UserDTO> users = userFeign.getByIds(participantsIds);
        Map<Long, byte[]> profileImages = profileImageFeign.getByUsersIds(participantsIds);
        Map<Long, String> presenceStatuses = presenceFeign.getStatuses(participantsIds);

        participantsIds.forEach(participantId -> {

            //Get actual participant data
            String name = users.get(participantId).getName();
            byte[] profileImage = profileImages.get(participantId);
            String presenceStatus = presenceStatuses.get(participantId);

            //Build participant
            ParticipantDTO participant = ParticipantDTO
                    .builder()
                    .id(participantId)
                    .name(name)
                    .profileImage(profileImage)
                    .presenceStatus(presenceStatus)
                    .build();
            participants.add(participant);
        });
        return participants;
    }

    @Autowired
    private UserFeign userFeign;
    @Autowired
    private ProfileImageFeign profileImageFeign;
    @Autowired
    private PresenceFeign presenceFeign;
}