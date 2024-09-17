package com.cesar.ConversationAPI.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ConversationDTO {
    private Long senderId;
    private Long recipientId;
    private String name;
    private int newMessagesAmount;
    private String recipientName;
    private String recipientPresenceStatus;
    private byte[] recipientProfileImage;
}