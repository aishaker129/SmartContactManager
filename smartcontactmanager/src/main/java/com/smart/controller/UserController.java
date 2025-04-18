package com.smart.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

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
	public void addCommonData(Model m,Principal p) {
		String userName =  p.getName();
//		System.out.println("USER "+userName);
		
		// get the user using username(Email)
		
		User user = userRepository.getUserByUserName(userName);
//		System.out.println("UserName "+user);
		m.addAttribute("user",user);
	}

	// dashboard home
	@GetMapping("/index")
	public String dshbord(Model m,Principal principal) {
		m.addAttribute("title","User Dashboard");
		
		return "Normal/user_dashbord";
	}
	
	// open add form handler
	@GetMapping("/add-contact")
	public String openAddContactForm(Model m) {
		
		m.addAttribute("title","Add Contact");
		m.addAttribute("contact",new Contact());
		
		
		return "Normal/add_contact_form";
	}
}
