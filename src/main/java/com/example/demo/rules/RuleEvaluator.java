package com.example.demo.rules;

import com.example.demo.entity.Expense;
import com.example.demo.rules.RuleEngine.Parsed;

public class RuleEvaluator {

    private RuleEvaluator() {}

    public static boolean evaluate(Parsed parsed, Expense expense) {
        if (parsed.field.equals("amount")) {
            int left = expense.getAmount() == null ? 0 : expense.getAmount();
            int right = Integer.parseInt(parsed.rawValue);

            return compareInt(left, parsed.op, right);
        }

        if (parsed.field.equals("claimType")) {
            String left = expense.getClaimType() == null ? "" : expense.getClaimType();
            String right = unquote(parsed.rawValue);

            return compareStr(left, parsed.op, right);
        }

        return false;
    }

    private static boolean compareInt(int left, String op, int right) {
        return switch (op) {
            case "<=" -> left <= right;
            case ">=" -> left >= right;
            case "==" -> left == right;
            case "!=" -> left != right;
            default -> false;
        };
    }

    private static boolean compareStr(String left, String op, String right) {
        return switch (op) {
            case "==" -> left.equals(right);
            case "!=" -> !left.equals(right);
            default -> false; // 文字列に <= >= は無し（ミニ版）
        };
    }

    private static String unquote(String s) {
        if (s == null || s.length() < 2) return "";
        return s.substring(1, s.length() - 1);
    }
}
