package com.cesar.ChatWeb.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;

@Entity()
@Table(name = "profileImages")
@Data
@Builder
public class ProfileImage {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY) 
	private Long id;
	private String name;
	private byte[] metadata;
}