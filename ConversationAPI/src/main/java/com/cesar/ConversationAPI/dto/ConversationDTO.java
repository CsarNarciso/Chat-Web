package com.cesar.ConversationAPI.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConversationDTO {

    private Long id;
    private Long senderId;
    private Long recipientId;
    private String name;
    private String imageName;
    private int newMessagesAmount;
}
