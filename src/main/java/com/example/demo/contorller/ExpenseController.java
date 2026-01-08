package com.example.demo.contorller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.demo.entity.User;
import com.example.demo.form.ExpenseForm;
import com.example.demo.service.ExpenseService;
import com.example.demo.util.Constants;

@Controller
public class ExpenseController {

	private final ExpenseService expenseService;

	public ExpenseController(ExpenseService expenseService) {
		this.expenseService=expenseService;
	}

	//画面表示時にFormを自動でmodelに入れる
	@ModelAttribute("expenseForm")
	public ExpenseForm expenseForm() {
		return new ExpenseForm();
	}

	@GetMapping("/travelCost")
	public String showTravelCost(Model model,HttpSession session) {

		//未ログインならログインへ
		User loginUser = (User) session.getAttribute("loginUser");
		if(loginUser == null) {

			return "redirect:/login";

		}
		return "travelCost";
	}

	@PostMapping("/travelCost")
	public String submitTravelCost(
			@Valid @ModelAttribute("expenseForm")ExpenseForm form,
			BindingResult result,
			HttpSession session,
			Model model) {

		User loginUser = (User) session.getAttribute("loginUser");
		if (loginUser == null) {
			return "redirect:/login";
		}

		if (result.hasErrors()) {
			return "travelCost";
		}

		expenseService.createExpense(form, loginUser);

		model.addAttribute("message",Constants.SUBMIT_EXPENSE);

		return "travelCost";
	}



}
