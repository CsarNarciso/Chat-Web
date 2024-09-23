package com.cesar.ImageService.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name="groups")
@Data
public class GroupImage {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    private Long groupId;
    private boolean defaultImage;
    private String name;
    private String extension;
    private String finalName;
}