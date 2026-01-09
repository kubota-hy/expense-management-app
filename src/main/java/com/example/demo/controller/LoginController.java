package com.example.demo.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import com.example.demo.util.Constants;

@Controller
public class LoginController {
	
	@Autowired
	private UserService userService;
	
	public LoginController(UserService userService) {
		
		this.userService = userService;
	}
	
	@GetMapping("/login")
	public String showLogin() {
		
		return "login";
	}
	
	@PostMapping("/login")
	public String login(
			@RequestParam String email,
			@RequestParam String password,
			HttpSession session,
			Model model) {
		
		User user = userService.login(email, password);
		
		if(user == null) {
			
			model.addAttribute("error",Constants.ERROR01);
	
		}
		
		session.setAttribute("loginUser", user);
		
		if("ADMIN".equals(user.getRole())) {
			
			return "redirect:/adminTravelCost";
			
		}else {
			
			return "redirect :/travelCost";
			
		}
		
	}

}
