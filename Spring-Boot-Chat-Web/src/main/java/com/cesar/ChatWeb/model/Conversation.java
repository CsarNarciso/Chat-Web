package com.cesar.ChatWeb.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="conversations")
public class Conversation {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private Long senderId;
	private Long recipientId;
	private String name;
	private String imageName;
	private int newMessagesAmount;


	public Conversation() {
		super();
	}



	
	public Long getId() {
		return id;
	}



	public void setId(Long id) {
		this.id = id;
	}



	public Long getSenderId() {
		return senderId;
	}



	public void setSenderId(Long senderId) {
		this.senderId = senderId;
	}



	public Long getRecipientId() {
		return recipientId;
	}



	public void setRecipientId(Long recipientId) {
		this.recipientId = recipientId;
	}



	public String getName() {
		return name;
	}



	public void setName(String name) {
		this.name = name;
	}



	public String getImageName() {
		return imageName;
	}



	public void setImageName(String imageName) {
		this.imageName = imageName;
	}



	public int getNewMessagesAmount() {
		return newMessagesAmount;
	}



	public void setNewMessagesAmount(int newMessagesAmount) {
		this.newMessagesAmount = newMessagesAmount;
	}
}
