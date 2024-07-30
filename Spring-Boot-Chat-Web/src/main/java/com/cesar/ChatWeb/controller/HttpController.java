 package com.cesar.ChatWeb.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.cesar.ChatWeb.model.User;
import com.cesar.ChatWeb.repository.ConversationRepository;
import com.cesar.ChatWeb.repository.UserRepository;
import com.cesar.ChatWeb.service.User_UserDetailsService;
import com.cesar.ChatWeb.validation.UserValidator;
import com.cesar.Methods.UpdateUserData;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class HttpController {


	@RequestMapping("/login")
	public String login() {
        return "login";			
	}



	@RequestMapping("/register")
	public String register(Model model){

		model.addAttribute("user", new UserValidator());

		return "register";
	}





	@PostMapping("/register/validate")
	public String validateRegistration(
	
		@Valid @ModelAttribute("user") UserValidator user,
		BindingResult validationResult,
		@RequestParam("imageMetadata") MultipartFile imageMetadata,
		HttpServletRequest httpRequest,
		Model model
		) {

		
		//Save original password for authentication
		String password = user.getPassword();



		//Validate


			//Existing email
		
		if ( userRepo.findByEmail(user.getEmail()).isPresent() ) {

			validationResult.addError(new FieldError("user", "email", "This email is already in use"));
		}


			//Existing name

		if ( userRepo.findByName(user.getName()).isPresent() ) {

			validationResult.addError(new FieldError("user", "name", "Name not available"));
		}
		

			
		//Incorrect

		if (validationResult.hasErrors()){

			System.out.println("Data validation wrong. User not saved in DB.");
			return "register";
		}

		//Correct

		else {


			//Upload user to DB

			User validatedUser = new User();

			validatedUser.setName( user.getName() );
			validatedUser.setEmail( user.getEmail() );
			validatedUser.setPassword(passwordEncoder.encode(user.getPassword()));

			userRepo.save(validatedUser);
			System.out.println("Uploading user " + validatedUser.getName() + " to DB...");



			//Storage profile image in server and DB 

			User storagedUser = userRepo.findByNameOrEmail(validatedUser.getName()).get();
			Long userId = storagedUser.getId();

			UpdateUserData userDataUpdater = new UpdateUserData(userRepo, conversationRepo);

			userDataUpdater.saveProfileImage(imageMetadata, userId);



			//Authenticate

			UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
				validatedUser.getName(),
				password
			);

			Authentication auth = autManager.authenticate(token);

			SecurityContext sc = SecurityContextHolder.getContext();

			sc.setAuthentication(auth);

			HttpSession session = httpRequest.getSession(true);

			session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, sc);


			
			//Allow access. Redirect to main chat page

			return "redirect:/chat";
		}
	}
	
	
	

	@PostMapping("/updateUserData")
	public String updateUserData( 
			@RequestParam(name = "newName", required = false) String newName,
			@RequestParam(name = "newImage", required = false) MultipartFile newImage,
			HttpServletRequest httpRequest, HttpSession session
			) {
		
		
		//Get data to update.
		
		Long actualUserId = (long) session.getAttribute("Id"); 
		
		//User name.
		
		if ( newName != null ) {
			
			
			//Validate.


			//Existing name.

			if ( userRepo.findByName( newName ) != null ) {
	
				//Incorrect.
				
				//Add result to validation in session.
				session.setAttribute("ValidationResult_UpdateName", "Incorrect");
				session.setAttribute("NameNotAvailable", newName);
				
				return "redirect:/chat_main";
			}
			
			//Successful.

			//Add update to session.
			session.setAttribute("Update", "Name");
			
			session.setAttribute("ValidationResult_UpdateName", "Successful");
			
			//Update in DB.
			
			userRepo.updateName( newName, actualUserId );
			
			//In authentication. 
			
			UsernamePasswordAuthenticationToken newToken = new UsernamePasswordAuthenticationToken( newName, null, null );

			SecurityContextHolder.getContext().setAuthentication( newToken );
		}
		
		//User image.
		
		else if ( newImage != null ) {
			
			//Update in DB and server files.
			
			new UpdateUserData(userRepo, conversationRepo).saveProfileImage(newImage, actualUserId);
			
			//Add update to session
			session.setAttribute("Update", "Image");
		}
		
		
		//Successful data update. Redirect to main chat page.
		
		return "redirect:/chat";
	}





	@RequestMapping({"/chat", "/"})
	public String chat(Model model, HttpSession session){
	
		
		//After update (?)
		
		String update = (String) session.getAttribute("Update");
		
		if ( update != null ) {
			
			//Name (?)
			
			if ( update == "Name" ) {
				
				//Get validation result.
				
				String validationResult_UpdateName = (String) session.getAttribute("ValidationResult_UpdateName"); 
				String nameNotAvailable = (String) session.getAttribute("NameNotAvailable"); 
				
					//and remove it from session.
				
				session.removeAttribute("ValidationResult_UpdateName");
				
				//Add result to model.
				
				model.addAttribute("ValidationResult_UpdateName", validationResult_UpdateName);
				
				//If validation was wrong, add name not available.
				
				if ( validationResult_UpdateName == "Incorrect" ) {
					
					model.addAttribute("NameNotAvailable", nameNotAvailable);
					
					//and delete it from session.
					session.removeAttribute("NameNotAvailable");
				}
			}
			
			//Image (?) Nothing to do.
			
			
			//Add update action to model.
			model.addAttribute("Update", update);	
			
				//and remove it from session.
			session.removeAttribute("Update");
		}
		
		
		//Load user data.

		//If data is not yet in session...
		if ( session.getAttribute("UserData") == null ) {
			
			Authentication a = SecurityContextHolder.getContext().getAuthentication();
			
			User u = userRepo.findByNameOrEmail( a.getName() ).get();
			
			//Load it in session.
			session.setAttribute("Id", u.getId());
			session.setAttribute("Name", u.getName());
			session.setAttribute("ImageName", u.getImageName());
		}
		
		//In model.
		model.addAttribute("Id", session.getAttribute("Id"));
		model.addAttribute("Name", session.getAttribute("Name"));
		model.addAttribute("ImageName", session.getAttribute("ImageName"));
		
		
		//Redirect to main chat page.
		return "chat_main";
	}




	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private AuthenticationManager autManager;
	@Autowired
	private User_UserDetailsService userDetailsService;
	@Autowired
	private UserRepository userRepo;
	@Autowired
	private ConversationRepository conversationRepo;
}
