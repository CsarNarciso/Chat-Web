package com.cesar.ChatWeb.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.cesar.ChatWeb.model.User;
import com.cesar.ChatWeb.repository.UserRepository;

public class User_UserDetailsService implements UserDetailsService {

	@Autowired
	private UserRepository userRepo;

	@Override
	public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {

		User user = userRepo.findByNameOrEmail(usernameOrEmail).get();

		if( user == null) {
			throw new UsernameNotFoundException("--User not found--");
		}
		else {
			System.out.println("User " + user.getName() + " found in DB.");
		}

		return org.springframework.security.core.userdetails.User.builder()
				.username(user.getName())
	            .password(user.getPassword())
	            .roles("USER")
	            .build();
	}
}