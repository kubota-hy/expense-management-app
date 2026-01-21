package com.example.demo.controller;

import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.demo.entity.Expense;
import com.example.demo.entity.User;
import com.example.demo.form.AdminExpenseSearchForm;
import com.example.demo.service.ExpenseService;
import com.example.demo.util.Constants;
import com.example.demo.util.Roles;
/**
 * 
 * 費用精算申請の管理を担当するコントローラークラス
 * 
 * @author Kubota
 */
@Controller
public class AdminExpenseController {

	private final ExpenseService expenseService;
	

	public AdminExpenseController(ExpenseService expenseService) {
		this.expenseService = expenseService;
	}
	
	/**
	 * 
	 * 管理者用の交通費申請一覧画面を表示する。
	 * 
	 * 未ログインの場合ログイン画面へ遷移
	 * 管理者権限でない場合もログイン画面へ遷移
	 * 管理者の場合、全申請データを取得して画面に渡す
	 * 
	 * @param session セッション情報
	 * @param model   画面表示モデル
	 * @return 管理者用交通費申請一覧画面
	 */
	@GetMapping("/adminTravelCost")
	public String showAdminTravelCost(HttpSession session, Model model) {
		User loginUser = (User) session.getAttribute("loginUser");
		if (loginUser == null) {
			return "redirect:/login";
		}

		if (!Roles.ADMIN.equals(loginUser.getRole())) {
			return "redirect:/login";
		}

		model.addAttribute("searchForm",new AdminExpenseSearchForm());
		model.addAttribute("expenseList", expenseService.findAllExpenses());

		return "admin/adminTravelCost";

	}
	/**
	 * 指定された交通費申請を承認する。
	 *
	 * 管理者のみ実行可能とし、
	 * 未ログインまたは管理者以外の場合はログイン画面へ遷移する。
	 *
	 * @param id      承認対象の申請ID
	 * @param session セッション情報
	 * @return 管理者用申請一覧画面へのリダイレクト
	 */
	@PostMapping("/admin/expenses/{id}/approve")
	public String approve(@PathVariable("id") Long id, HttpSession session) {
		User loginUser = (User) session.getAttribute("loginUser");
		if (loginUser == null || !Roles.ADMIN.equals(loginUser.getRole())) {
			return "redirect:/login";
		}

		expenseService.approveExpenses(id);
		return "redirect:/adminTravelCost";
	}
	
	/**
	 * 指定された交通費申請を却下する。
	 *
	 * 管理者のみ実行可能とし、
	 * 未ログインまたは管理者以外の場合はログイン画面へ遷移する。
	 *
	 * @param id      却下対象の申請ID
	 * @param session セッション情報
	 * @return 管理者用申請一覧画面へのリダイレクト
	 */
	@PostMapping("/admin/expenses/{id}/reject")
	public String reject(@PathVariable("id") Long id, HttpSession session) {
		User loginUser = (User) session.getAttribute("loginUser");
		if (loginUser == null || !Roles.ADMIN.equals(loginUser.getRole())) {
			return "redirect:/login";
		}

		expenseService.rejectExpenses(id);
		return "redirect:/adminTravelCost";
	}
	
	/**
	 * 指定された申請を差戻しする。
	 *
	 * 管理者のみ実行可能とし、
	 * 未ログインまたは管理者以外の場合はログイン画面へ遷移する。
	 *
	 * @param id      差戻し対象の申請ID
	 * @param session セッション情報
	 * @return 管理者用申請一覧画面へのリダイレクト
	 */
	@PostMapping("/admin/expenses/{id}/return")
	public String returnExpense(
	        @PathVariable("id") Long id,
	        HttpSession session) {

	    User loginUser = (User) session.getAttribute("loginUser");
	    if (loginUser == null || !Roles.ADMIN.equals(loginUser.getRole())) {
	        return "redirect:/login";
	    }

	    // 今回は理由を固定文言で差戻し（ミニ版）
	    expenseService.returnExpense(
	            id,
	            Constants.RETURN_SIBMITT
	    );

	    return "redirect:/adminTravelCost";
	}

	
	/**
	 * 管理者用の交通費申請を検索する。
	 *
	 * 画面の検索条件を受け取り、Serviceに検索処理を委譲する。
	 * 未ログインまたは管理者以外の場合はログイン画面へ遷移する。
	 *
	 * @param form    管理者用検索フォーム
	 * @param session セッション情報
	 * @param model   画面表示モデル
	 * @return 管理者用交通費申請一覧画面（検索結果を表示）
	 */
	@PostMapping("/adminTravelCost/search")
	public String searchAdminTravelCost(
	        @ModelAttribute("searchForm") AdminExpenseSearchForm form,
	        HttpSession session,
	        Model model) {

	    User loginUser = (User) session.getAttribute("loginUser");
	    if (loginUser == null || !Roles.ADMIN.equals(loginUser.getRole())) {
	        return "redirect:/login";
	    }

	    
	    List<Expense> expenseList = expenseService.searchExpensesForAdmin(form);

	    model.addAttribute("expenseList", expenseList);

	    return "admin/adminTravelCost";

	}
	
}
