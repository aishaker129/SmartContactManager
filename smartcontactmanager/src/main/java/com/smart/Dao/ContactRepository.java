package com.smart.Dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smart.entities.Contact;
import com.smart.entities.User;

public interface ContactRepository extends JpaRepository<Contact, Integer> {

	@Query("from Contact as c where c.user.id =:userId")
	
	// Pageable has two type of value
	// current page
	// contact per page
	public Page<Contact> findContactByUser(@Param("userId")int userId, Pageable pageable);
	
	//Search
	public List<Contact> findByNameContainingAndUser(String name,User user);
}
