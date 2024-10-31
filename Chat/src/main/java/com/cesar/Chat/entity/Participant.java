package com.cesar.Chat.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;
import java.io.Serializable;

@Builder
@Data
@Entity
@Table(name = "participants")
public class Participant implements Serializable {
    @Id
    private Long id;
}