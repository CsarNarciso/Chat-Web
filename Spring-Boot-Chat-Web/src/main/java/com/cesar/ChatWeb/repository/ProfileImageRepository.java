package com.cesar.ChatWeb.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cesar.ChatWeb.model.ProfileImage;

public interface ProfileImageRepository extends JpaRepository<ProfileImage, Long> {
	
	ProfileImage findByName(String name);
}
