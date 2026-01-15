package com.example.demo.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.entity.Expense;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findByUserIdOrderByUseDateDesc(Long userId);
    
    List<Expense> findAllByOrderBySubmittedAtDesc();
    
    @Query("""
    		  select e from Expense e
    		  where (:status is null or :status = '' or e.status = :status)
    		    and (:claimType is null or :claimType = '' or e.claimType = :claimType)
    		    and e.submittedAt >= :from
    		    and e.submittedAt <  :to
    		    and (
    		      :name is null or :name = '' or
    		      exists (
    		        select 1 from User u
    		        where u.id = e.userId
    		          and u.name like concat('%', :name, '%')
    		      )
    		    )
    		  order by e.id desc
    		""")
    		List<Expense> searchForAdmin(
    		    @Param("name") String name,
    		    @Param("status") String status,
    		    @Param("claimType") String claimType,
    		    @Param("from") LocalDateTime from,
    		    @Param("to") LocalDateTime to
    		);


}
