package com.cesar.ProfileImageService.repository;

import com.cesar.ProfileImageService.entity.ProfileImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileImageRepository extends JpaRepository<ProfileImage, Long> {
}
