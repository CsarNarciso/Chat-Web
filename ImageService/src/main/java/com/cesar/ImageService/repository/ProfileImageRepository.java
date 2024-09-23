package com.cesar.ImageService.repository;

import com.cesar.ImageService.entity.ProfileImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileImageRepository extends JpaRepository<ProfileImage, Long> {
    ProfileImage findByUserId(Long userId);
}