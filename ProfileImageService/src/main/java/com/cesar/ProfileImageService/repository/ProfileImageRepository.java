package com.cesar.ProfileImageService.repository;

import com.cesar.ProfileImageService.entity.ProfileImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileImageRepository extends JpaRepository<ProfileImage, Long> {

    Optional<ProfileImage> findByUserId(Long userId);
}
