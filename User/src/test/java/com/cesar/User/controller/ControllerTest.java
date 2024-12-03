package com.cesar.User.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.cesar.User.service.UserService;

@ExtendWith(MockitoExtension.class)
public class ControllerTest {

	@Test
	public void testSomethingHere() {
		
	}
	
	
	
	
	@Mock
	private UserService service;
	
	@InjectMocks
	private Controller controller;
}
