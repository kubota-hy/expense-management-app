package com.example.demo.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "expenses")
public class Expense {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name="user_id", nullable = false)
    private Long userId;

    @Column(name="use_date", nullable = false)
    private LocalDate useDate;

    @Column(name="claim_type", nullable = false, length = 20)
    private String claimType; // 交通費 / その他

    @Column(length = 50)
    private String transportation;

    @Column(name="from_place", length = 100)
    private String fromPlace;

    @Column(name="to_place", length = 100)
    private String toPlace;

    @Column(name="trip_type", length = 10)
    private String tripType; // 片道 / 往復

    @Column(nullable = false)
    private Integer amount;

    @Column(nullable = false, length = 200)
    private String purpose;

    @Column(length = 500)
    private String note;

    @Column(name="status", nullable = false, length = 20)
    private String status = "SUBMITTED";

    @Column(name="submitted_at", nullable = false)
    private LocalDateTime submittedAt;

    @PrePersist
    public void onCreate() {
        this.submittedAt = LocalDateTime.now();
        
    }
}
