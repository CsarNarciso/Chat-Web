package com.cesar.User.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

@Entity
@Table(name="users")
@Data
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String email;
    private String password;
    @Column(name="profile_image_url")
    private String profileImageUrl;
}