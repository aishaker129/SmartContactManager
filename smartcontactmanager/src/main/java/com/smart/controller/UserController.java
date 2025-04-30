package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.smart.Dao.ContactRepository;
import com.smart.Dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import com.smart.helper.Message;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ContactRepository contactRepository;

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

	 @InitBinder
	 public void initBinder(WebDataBinder binder) {
		    binder.setDisallowedFields("image");
		}
	 
	@PostMapping("/process-contact")
	public String processingContactForm(@Valid @ModelAttribute Contact contact,
			BindingResult result,@RequestParam("image") MultipartFile file,
			Model m,Principal p, HttpSession session) {
		try {
			String name = p.getName();
			User user = this.userRepository.getUserByUserName(name);
			
			
			if (result.hasErrors()) {
				System.out.println("Error: " + result.toString());
				m.addAttribute("contact", contact);
				return "Normal/add_contact_form";
			}
			
			// Processing and uploading file
			
			if(file.isEmpty()) {
				System.out.println("Empty image !!");
				contact.setImage("contact.png");
				throw new Exception("You have upload image");
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
			
			m.addAttribute("contact",new Contact());
			// Success Message show call
			session.setAttribute("message",new Message("Contact added successfully !!", "alert-success"));
		} catch (Exception e) {
			System.out.println("ERROR: " + e.getMessage());
			e.printStackTrace();
			
			// Error message show call
			session.setAttribute("message",new Message("Something went to wrong !!", "alert-danger"));
		}
		return "Normal/add_contact_form";
	}
	
	@GetMapping("/view_contact/{page}")
	public String ShowContacts(@PathVariable("page") Integer page,Model m, Principal p) {
		
		m.addAttribute("title", "All Contacts");
		String userName = p.getName();
		
		User user = this.userRepository.getUserByUserName(userName);
		
		Pageable pageable = PageRequest.of(page, 2);
		
		Page<Contact> contact =  this.contactRepository.findContactByUser(user.getId(),pageable);
		
		m.addAttribute("contacts",contact);
		m.addAttribute("currentPage",page);
		m.addAttribute("totalPages",contact.getTotalPages());
		
		return "Normal/show_contact";
		
		
	}
	
	@GetMapping("/{cid}/contact")
	public String showContactDetails(@PathVariable("cid") Integer cid,Model m,Principal p) {
		System.out.println("Contact_ID: "+cid);
		
		Optional<Contact> contactOptional = this.contactRepository.findById(cid);
		Contact contact = contactOptional.get(); 
		
		String userName = p.getName();
		User user = this.userRepository.getUserByUserName(userName);
		
		if(user.getId()==contact.getUser().getId()) {
			m.addAttribute("contact",contact);
			m.addAttribute("title",contact.getName());
		}
		
		
		
		return "Normal/contact_details";
	}
	
	// delete contact handler
	@GetMapping("/delete/{cid}")
	public String deleteContact(@PathVariable("cid") Integer cid,Principal p,HttpSession session) {
		
		Optional<Contact> contactOptional = this.contactRepository.findById(cid);
		Contact contact = contactOptional.get();
		
		String userName = p.getName();
		User user = this.userRepository.getUserByUserName(userName);
		
		//check assignment...
		if(user.getId()==contact.getUser().getId()) {
			// contact.setUser(null); // first we set null contact in user site then delete the contact because user and contact entity are mapped 
			
			// remove image
			// contact 
			try {
	            File deleteFile = new ClassPathResource("static/Image/").getFile();
	            File file = new File(deleteFile, contact.getImage());
	            if (file.exists()) {
	                file.delete();
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
			this.contactRepository.delete(contact);
			session.setAttribute("message",new Message("Contact deleted successfully", "alert-success"));
		}
		else {
			session.setAttribute("message",new Message("Something went to wrong..", "alert-danger"));
		}
		
		
		return "redirect:/user/view_contact/0";
	}
	
	
	
	//Open update form handler
	@PostMapping("/update_contact/{cid}")
	public String updateHandler(@PathVariable("cid") Integer cid,Model m) {
		m.addAttribute("title","Update Contact");
		
		Contact contact = this.contactRepository.findById(cid).get();
		m.addAttribute("contact",contact);
		return "Normal/update_contact";
	}
	
	// update contact handler
	//@PostMapping("/process-Update")
	@RequestMapping(value = "/process-Update",method = RequestMethod.POST)
	public String processUpdate(@ModelAttribute Contact contact,
			@RequestParam("image") MultipartFile file,Model m, 
			HttpSession session,Principal p) {
		
		try {
			
			// old contact details
			Contact oldContact = this.contactRepository.findById(contact.getCid()).get();
			
			if(!file.isEmpty()) {
				// file work...
				// rewrite...
				
				// delete old photo
				File deleteFile = new ClassPathResource("static/Image").getFile();
				File file1 = new File(deleteFile, oldContact.getImage());
				file1.delete();
				// update new photo
				File imageFolder = new ClassPathResource("static/Image").getFile();
				Path path = Paths.get(imageFolder.getAbsolutePath()+File.separator+file.getOriginalFilename());
			
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				
				contact.setImage(file.getOriginalFilename());
			}
			else {
				contact.setImage(oldContact.getImage());
			}
			
			User user = this.userRepository.getUserByUserName(p.getName());
			contact.setUser(user);
			
			this.contactRepository.save(contact);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Contact : "+contact.getName());
		return "redirect:/user/"+contact.getCid()+"/contact";
	}

	// user profile handler
	@GetMapping("/profile")
	public String userProfile(Model m) {
		m.addAttribute("title","User Profile");
		return "Normal/user_profile";
	}
	
	@PostMapping("/user_update/{id}")
	public String updateUser(@PathVariable("id") Integer id, Principal p,Model m) {
		m.addAttribute("title","Update User");
		
		User user = this.userRepository.findById(id).get();
		m.addAttribute("user",user);
		return "Normal/user_update";
	}
	
	@InitBinder
	 public void initBinder1(WebDataBinder binder) {
		    binder.setDisallowedFields("imageUrl");
		}
	
	@PostMapping("/user-process-Update")
	public String processUpdateUser(@ModelAttribute User user,@RequestParam("imageUrl") 
	MultipartFile file,Model m,HttpSession session, Principal p) {
		try {
			User oldUser = this.userRepository.findById(user.getId()).get();
			
			if(!file.isEmpty()) {
				// file work...
				// rewrite...
				
				// delete old photo
				File deleteFile = new ClassPathResource("static/Image").getFile();
				File file1 = new File(deleteFile, oldUser.getImageUrl());
				file1.delete();
				// update new photo
				File imageFolder = new ClassPathResource("static/Image").getFile();
				Path path = Paths.get(imageFolder.getAbsolutePath()+File.separator+file.getOriginalFilename());
			
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				
				user.setImageUrl(file.getOriginalFilename());
			}
			else {
				user.setImageUrl(oldUser.getImageUrl());
			}
			
			this.userRepository.save(user);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		System.out.println("User: "+user);
		return "redirect:/user/profile";
	}
	
	@GetMapping("/deleteUser/{id}")
	public String deleteUser(@PathVariable("id") Integer id,Principal p,HttpSession session) {
		User user = this.userRepository.findById(id).get();
		
		// contact.setUser(null); // first we set null contact in user site then delete the contact because user and contact entity are mapped 
		
					// remove image
					// contact 
					try {
			            File deleteFile = new ClassPathResource("static/Image/").getFile();
			            File file = new File(deleteFile, user.getImageUrl());
			            if (file.exists()) {
			                file.delete();
			            }
			            
			            this.userRepository.delete(user);
						session.setAttribute("message",new Message("User deleted successfully", "alert-success"));
			        } catch (Exception e) {
			            e.printStackTrace();
						session.setAttribute("message",new Message("Something Went to Wrong", "alert-danger"));
			        }
					
		
		return "redirect:/signin";
	}
}
