package com.example.demo.rules;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.entity.Expense;
import com.example.demo.entity.Rule;
import com.example.demo.repository.RuleRepository;

@Service
public class RuleEngine {

    private final RuleRepository ruleRepository;

    public RuleEngine(RuleRepository ruleRepository) {
        this.ruleRepository = ruleRepository;
    }

    // ルールが壊れてても落とさずスキップする方針
    public Decision decide(Expense expense) {
        List<Rule> rules = ruleRepository.findByEnabledTrueOrderByPriorityAsc();

        for (Rule rule : rules) {
            Parsed parsed = RuleParser.tryParse(rule.getRuleText());
            if (parsed == null) continue;

            if (RuleEvaluator.evaluate(parsed, expense)) {
                return parsed.decision;
            }
        }

        // デフォルト
        return Decision.NEED_REVIEW;
    }

    // パース結果
    static class Parsed {
        final String field;      // amount / claimType
        final String op;         // <= >= == !=
        final String rawValue;   // "交通費" or 3000
        final Decision decision;

        Parsed(String field, String op, String rawValue, Decision decision) {
            this.field = field;
            this.op = op;
            this.rawValue = rawValue;
            this.decision = decision;
        }
    }
}
