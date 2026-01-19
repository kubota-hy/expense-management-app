package com.example.demo.rules;

import com.example.demo.rules.RuleEngine.Parsed;

public class RuleParser {

    private RuleParser() {}

    public static Parsed tryParse(String ruleText) {
        // 期待例: IF amount <= 3000 THEN APPROVE
        if (ruleText == null) return null;

        String t = ruleText.trim();

        // IF と THEN が揃ってること
        if (!t.startsWith("IF ")) return null;
        int thenIdx = t.indexOf(" THEN ");
        if (thenIdx < 0) return null;

        String cond = t.substring(3, thenIdx).trim();      // amount <= 3000
        String decisionStr = t.substring(thenIdx + 6).trim(); // APPROVE

        Decision decision;
        try {
            decision = Decision.valueOf(decisionStr);
        } catch (Exception e) {
            return null;
        }

        // cond を3トークンに分解: field op value
        String[] parts = cond.split("\\s+", 3);
        if (parts.length != 3) return null;

        String field = parts[0];
        String op = parts[1];
        String rawValue = parts[2].trim();

        // field制限
        if (!field.equals("amount") && !field.equals("claimType")) return null;

        // op制限
        if (!(op.equals("<=") || op.equals(">=") || op.equals("==") || op.equals("!="))) return null;

        // rawValue の最小検証（文字列は "..." のみ許可）
        if (field.equals("claimType")) {
            if (!(rawValue.startsWith("\"") && rawValue.endsWith("\"") && rawValue.length() >= 2)) return null;
        } else { // amount
            try {
                Integer.parseInt(rawValue);
            } catch (NumberFormatException e) {
                return null;
            }
        }

        return new Parsed(field, op, rawValue, decision);
    }
}
