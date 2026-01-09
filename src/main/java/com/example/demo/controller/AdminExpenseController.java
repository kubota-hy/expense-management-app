package com.example.demo.controller;

import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.entity.Expense;
import com.example.demo.entity.User;
import com.example.demo.repository.ExpenseRepository;

@Controller
public class AdminExpenseController {
	
	private ExpenseRepository expenseRepository;
	
	public AdminExpenseController(ExpenseRepository expenseRepository) {
		this.expenseRepository=expenseRepository;
	}
	
	@GetMapping("/adminTravelCost")
	public String showAdminTravelCost(HttpSession session,Model model) {
		
		User loginUser = (User) session.getAttribute("loginUser");
		if(loginUser == null) {
			
			return "redirect:/login";
			
		}
		
		List<Expense>expenseList = expenseRepository.findAll();
		model.addAttribute("expenseList", expenseList);
		
		return "adminTravelCost";
	}

}
