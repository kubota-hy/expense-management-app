package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Expense;
import com.example.demo.entity.User;

public interface ExpenseRepository extends JpaRepository<Expense,Long>{
	
	List<Expense> findAll();
	
	List<Expense> findByUserOrderByUseDateDesc(User user);


}
