package com.example.demo.form;

import java.time.LocalDate;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter
public class ExpenseForm {
	
	@NotNull
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate useDate;
	
	@NotBlank
	private String claimType;
	
	@NotNull
	@Min(0)
	private Integer amount;
	
	@NotBlank
	@Size(max = 500)
	private String purpose;
	
	@Size(max = 500)
	private String note;

}
