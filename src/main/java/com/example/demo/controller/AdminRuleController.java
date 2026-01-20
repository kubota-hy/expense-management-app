package com.example.demo.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.entity.Rule;
import com.example.demo.service.RuleService;
/**
 * 管理者向けの費用精算申請および申請ルール管理を担当するコントローラークラス。
 *
 * 本コントローラーでは以下の機能を提供する。
 * ・管理者用費用申請一覧の表示
 * ・費用申請の承認および却下処理
 * ・申請条件に基づく検索処理
 * ・申請自動判定ルールの一覧表示および有効／無効の切替
 *
 * すべての管理者向け機能は、ログイン済みかつ管理者権限（ADMIN）を持つユーザーのみ
 * 実行可能とし、それ以外の場合はログイン画面へリダイレクトする。
 */

@Controller
public class AdminRuleController {

    private final RuleService ruleService;

    public AdminRuleController(RuleService ruleService) {
        this.ruleService = ruleService;
    }

    /**
     * 管理者向けに申請自動判定ルールの一覧画面を表示する。
     *
     * ルールは優先順位順で取得し、画面上で有効／無効状態を確認できる。
     *
     * @param model 画面表示用モデル
     * @return 管理者用ルール一覧画面
     */
    @GetMapping("/admin/rules")
    public String ruleList(Model model) {
        List<Rule> rules = ruleService.findAllOrderByPriority();
        model.addAttribute("rules", rules);
        return "admin/ruleList";
    }

    /**
     * 指定された申請自動判定ルールの有効／無効を切り替える。
     *
     * 有効なルールは申請作成時の自動判定に使用され、
     * 無効なルールは判定対象外となる。
     * 切替後はルール一覧画面へリダイレクトする。
     *
     * @param ruleId 切替対象のルールID
     * @return ルール一覧画面へのリダイレクト
     */
    @PostMapping("/admin/rules/toggle")
    public String toggleRule(@RequestParam Long ruleId) {
        ruleService.toggleEnabled(ruleId);
        return "redirect:/admin/rules";
    }
}

