package com.example.demo.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.entity.User;
import com.example.demo.form.AdminUserCreateForm;
import com.example.demo.service.AdminUserService;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {
	
	private final AdminUserService adminUserService;
	
	public AdminUserController(AdminUserService adminUserService) {
		this.adminUserService  = adminUserService;
	}
	
	@PostMapping
	public String create(
	        @Valid @ModelAttribute("createForm") AdminUserCreateForm form,
	        BindingResult bindingResult,
	        HttpSession session,
	        Model model) {

	    User loginUser = (User) session.getAttribute("loginUser");
	    if (loginUser == null || !"ADMIN".equals(loginUser.getRole())) {
	        return "redirect:/login";
	    }

	    if (bindingResult.hasErrors()) {
	        return "adminUserNew";
	    }

	    try {
	        adminUserService.createUserByAdmin(form);
	    } catch (IllegalArgumentException e) {
	        model.addAttribute("globalError", e.getMessage());
	        return "adminUserNew";
	    }

	    return "redirect:/top";
	}
	

}