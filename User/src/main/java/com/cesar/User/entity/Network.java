//package com.cesar.User.entity;
//
//import java.io.Serializable;
//import java.util.List;
//import java.util.UUID;
//
//import jakarta.persistence.Column;
//import jakarta.persistence.Entity;
//import jakarta.persistence.GeneratedValue;
//import jakarta.persistence.GenerationType;
//import jakarta.persistence.Id;
//import jakarta.persistence.Table;
//import lombok.Data;
//
//@Entity
//@Table(name="networks")
//@Data
//public class Network implements Serializable {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//    @Column(name="user_id")
//    private Long userId;
//    @Column(name="conversation_ids")
//    private List<UUID> conversationIds;
//}