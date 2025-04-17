package com.smart.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.smart.Dao.UserRepository;
import com.smart.entities.User;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private UserRepository userRepository;

	@GetMapping("/index")
	public String dshbord(Model m,Principal principal) {
		String userName =  principal.getName();
//		System.out.println("USER "+userName);
		
		// get the user using username(Email)
		
		User user = userRepository.getUserByUserName(userName);
//		System.out.println("UserName "+user);
		m.addAttribute("user",user);
		return "Normal/user_dashbord";
	}
}
