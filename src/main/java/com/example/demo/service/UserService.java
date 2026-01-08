package com.example.demo.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;

@Service
public class UserService {
	
	private final UserRepository userRepository;
	
	public UserService(UserRepository userRepository) {
		
		this.userRepository = userRepository;
		
	}
	
	public User login(String email,String password) {
		
		Optional<User> optUser = userRepository.findByEmail(email);
		
		if(optUser.isEmpty()) {
			return null;
		}
		
		User user = optUser.get();
		
		if(!user.getPasswordHash().equals(password)) {
			
			return null;
		}
		
		if(!user.isEnabled()) {
			
			return null;
		}
		
		return user;
	}

}
