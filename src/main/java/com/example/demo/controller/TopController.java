package com.example.demo.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.entity.User;
import com.example.demo.util.Roles;

@Controller
public class TopController {

	@GetMapping("/top")
	public String top(HttpSession session) {
		User user = (User) session.getAttribute("loginUser");
		if (user == null || !Roles.ADMIN.equals(user.getRole())) {
			return "redirect:/login";
		}
		return "top";
	}

}


