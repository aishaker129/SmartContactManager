package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.Dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserRepository userRepository;

	// method for adding common data to response
	@ModelAttribute
	public void addCommonData(Model m, Principal p) {
		String userName = p.getName();
//		System.out.println("USER "+userName);

		// get the user using username(Email)

		User user = userRepository.getUserByUserName(userName);
//		System.out.println("UserName "+user);
		m.addAttribute("user", user);
	}

	// dashboard home
	@GetMapping("/index")
	public String dshbord(Model m, Principal principal) {
		m.addAttribute("title", "User Dashboard");

		return "Normal/user_dashbord";
	}

	// open add form handler
	@GetMapping("/add-contact")
	public String openAddContactForm(Model m) {

		m.addAttribute("title", "Add Contact");
		m.addAttribute("contact", new Contact());

		return "Normal/add_contact_form";
	}

	@PostMapping("/process-contact")
	public String processingContactForm(@ModelAttribute Contact contact,
			@RequestParam("profileImage") MultipartFile file,
			Principal p) {
		try {
			String name = p.getName();
			User user = this.userRepository.getUserByUserName(name);
			
			// Processing and uploading file
			if(file.isEmpty()) {
				//kico korbo pore
			}
			else {
				// upload the file
				contact.setImage(file.getOriginalFilename());
				
				 File imageFolder = new ClassPathResource("static/Image").getFile();

				    if (!imageFolder.exists()) {
				        imageFolder.mkdirs();
				    }
	            
				Path path = Paths.get(imageFolder.getAbsolutePath()+File.separator+file.getOriginalFilename());
				
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				
				}
			
			contact.setUser(user);
			user.getContact().add(contact);
			this.userRepository.save(user);
			System.out.println("contact = " + contact);
			System.out.println("Contact Add successfully");
		} catch (Exception e) {
			System.out.println("ERROR: " + e.getMessage());
			e.printStackTrace();
		}
		return "Normal/add_contact_form";
	}
}
