package com.example.demo.controller;

import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.entity.Expense;
import com.example.demo.entity.User;
import com.example.demo.service.ExpenseService;

@Controller
public class AdminExpenseController {

	private ExpenseService expenseService;

	public AdminExpenseController(ExpenseService expenseService) {
		this.expenseService=expenseService;
	}

	@GetMapping("/adminTravelCost")
	public String showAdminTravelCost(HttpSession session,Model model) {

		User loginUser = (User) session.getAttribute("loginUser");
		if(loginUser == null) {

			return "redirect:/login";

		}

		if(!"ADMIN".equals(loginUser.getRole())) {

			return "redirect:/login";

		}

		List<Expense>expenseList = expenseService.findAllExpenses();
		model.addAttribute("expenseList", expenseList);

		return "adminTravelCost";
	}

}
