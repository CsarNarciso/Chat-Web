package com.cesar.ProfileImage.repository;

import com.cesar.ProfileImage.entity.ProfileImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileImageRepository extends JpaRepository<ProfileImage, Long> {
    ProfileImage findByUserId(Long userId);
}