package com.smart.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.smart.Dao.UserRepository;
import com.smart.entities.User;

public class UserDetailsServiceImpl implements UserDetailsService {
	
	@Autowired
	private UserRepository userRepository; 
	@Override	
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// fetching user data from database
		User user = userRepository.getUserByUserName(username);
		
		if(user == null) {
			throw new UsernameNotFoundException("Could not found user !!");	
		}
		
		CustomUserDelails customUserDetails = new CustomUserDelails(user);
		return customUserDetails;
	}

}
