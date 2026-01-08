package com.example.demo.service;

import org.springframework.stereotype.Service;

import com.example.demo.entity.Expense;
import com.example.demo.entity.User;
import com.example.demo.form.ExpenseForm;
import com.example.demo.repository.ExpenseRepository;

@Service
public class ExpenseService {
	
	private final ExpenseRepository expenseRepository;
	
	public ExpenseService(ExpenseRepository expenseRepository) {
		this.expenseRepository = expenseRepository;
	}
	
	public void createExpense(ExpenseForm form,User loginUser) {
		
		Expense expense = new Expense();
		
		expense.setUserId(loginUser.getId());
		expense.setUseDate(form.getUseDate());
		expense.setClaimType(form.getClaimType());
		expense.setAmount(form.getAmount());
		expense.setPurpose(form.getPurpose());
		expense.setNote(form.getNote());
		
		expenseRepository.save(expense);
	}

}
