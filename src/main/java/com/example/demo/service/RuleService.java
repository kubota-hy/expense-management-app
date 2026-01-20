package com.example.demo.service;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Rule;
import com.example.demo.repository.RuleRepository;

@Service
public class RuleService {
	
	private final RuleRepository ruleRepository;
	
	public RuleService(RuleRepository ruleRepository) {
		this.ruleRepository=ruleRepository;
	}
	
	public List<Rule> findAllOrderByPriority() {
	    return ruleRepository.findAll(Sort.by("priority"));
	}

	public void toggleEnabled(Long ruleId) {
	    Rule rule = ruleRepository.findById(ruleId)
	        .orElseThrow(() -> new IllegalArgumentException("rule not found"));

	    rule.setEnabled(!rule.getEnabled());
	    ruleRepository.save(rule);
	}


}
