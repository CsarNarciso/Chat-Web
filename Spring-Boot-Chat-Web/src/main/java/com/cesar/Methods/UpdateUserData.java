package com.cesar.Methods;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.cesar.ChatWeb.repository.ConversationRepository;
import com.cesar.ChatWeb.repository.UserRepository;

@Component
public class UpdateUserData {

	public UpdateUserData(UserRepository userRepo, ConversationRepository conversationRepo) {
		this.userRepo = userRepo;
		this.conversationRepo = conversationRepo;
	}


	public String saveProfileImage(MultipartFile imageMetadata, Long userId) {

			String imageName = "NoProfileImage.png";
			String extension = null;

			if(!imageMetadata.isEmpty()) {

				System.out.println("*****************IMAGE METADATA IS NOT EMPTY");
				
				imageName = String.valueOf(userId);
				String extensionType = imageMetadata.getContentType();

				if( extensionType.equals("image/jpeg") ) {
					extension = ".jpg";
				}
				else if ( extensionType.equals("image/png") ) {
					extension = ".png";
				}

				System.out.println(imageName + extension);
				
				File image = new File("/images/profile/" + imageName + extension);

				if (image.exists()) {

					System.out.println("Replacing preivous image...");
					image.delete();

				}

				try {
					imageMetadata.transferTo(image);
					System.out.println("Profile image storaged in server.");
					imageName = userId + extension;
				}
				catch(Exception e) { e.printStackTrace(); }
			}

			userRepo.updateImageName(imageName, userId);

			conversationRepo.updateImageNameByUserId(userId, imageName);

			return imageName;
		}

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private ConversationRepository conversationRepo;
}