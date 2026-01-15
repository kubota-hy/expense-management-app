package com.example.demo.form;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminExpenseSearchForm {
	
	private String userName;
    private String status;
    private String claimType;

    // 申請日（submitted_at）
    private String submittedFrom;
    private String submittedTo;
}
