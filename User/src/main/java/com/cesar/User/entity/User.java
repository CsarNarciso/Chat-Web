package com.cesar.User.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import java.io.Serializable;

@Entity
@Table(name="users")
@Data
@Builder
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String email;
    private String password;
    @Column(name="profile_image_url")
    private String profileImageUrl;
}