package com.example.demo.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.demo.entity.UserEntity;
import com.example.demo.repository.UserRepository;

@Service
public class UserService {
	
	private final UserRepository userRepository;
	
	public UserService(UserRepository userRepository) {
		
		this.userRepository = userRepository;
		
	}
	
	public UserEntity login(String email,String password) {
		
		Optional<UserEntity> optUser = userRepository.findByEmail(email);
		
		if(optUser.isEmpty()) {
			return null;
		}
		
		UserEntity user = optUser.get();
		
		if(!user.getPasswordHash().equals(password)) {
			
			return null;
		}
		
		if(!user.isEnabled()) {
			
			return null;
		}
		
		return user;
	}

}
