package com.cesar.ChatWeb.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProfileImageDTO {

	private String name;
	private byte[] metadata;
}
