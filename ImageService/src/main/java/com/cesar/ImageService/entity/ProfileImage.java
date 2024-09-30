package com.cesar.ImageService.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

@Entity
@Table(name= "profiles")
@Data
@Builder
public class ProfileImage {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private boolean defaultImage;
    private String name;
    private String extension;
    private String finalName;
}