package com.example.demo.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.entity.User;

@Controller
public class TopController {

	@GetMapping("/top")
	public String top(HttpSession session) {
		User user = (User) session.getAttribute("loginUser");
		if (user == null || !"ADMIN".equals(user.getRole())) {
			return "redirect:/login";
		}
		return "top";
	}

}


