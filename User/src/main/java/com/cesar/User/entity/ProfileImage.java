package com.cesar.User.entity;

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
    @Column(name="user_id")
    private Long userId;
    @Column(name="default_image")
    private boolean defaultImage;
    private String name;
    private String extension;
    @Column(name="final_name")
    private String finalName;
}