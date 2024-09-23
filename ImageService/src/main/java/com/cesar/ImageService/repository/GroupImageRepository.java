package com.cesar.ImageService.repository;

import com.cesar.ImageService.entity.GroupImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupImageRepository extends JpaRepository<GroupImage, Long> {
    GroupImage findByGroupId(Long conversationId);
}