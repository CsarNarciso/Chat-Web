package com.cesar.ProfileImageService.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;

@Entity
@Table(name="profileImages")
@Data
@Builder
public class ProfileImage {
    private Long userId;
    private boolean defaultImage;
    private String name;
    private String extension;
    private String finalName;
}