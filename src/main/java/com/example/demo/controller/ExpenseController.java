package com.example.demo.controller;

import java.util.List;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.entity.Expense;
import com.example.demo.entity.User;
import com.example.demo.form.ExpenseForm;
import com.example.demo.service.ExpenseService;
import com.example.demo.util.Constants;

/**
 * 一般ユーザー向けの交通費申請機能を扱うコントローラクラス。
 * 申請一覧の表示、申請登録（送信）を担当する。
 *
 * 未ログインの場合はログイン画面へリダイレクトする。
 *
 * @author Kubota
 */
@Controller
public class ExpenseController {

	/** 交通費申請に関する業務処理を行うサービス */
	private final ExpenseService expenseService;

	/** セッションからログイン情報を取得するために使用 */
	@Autowired
	private HttpSession session;

	/**
	 * コンストラクタインジェクションによりサービスを受け取る。
	 *
	 * @param expenseService 交通費申請サービス
	 */
	public ExpenseController(ExpenseService expenseService) {
		this.expenseService = expenseService;
	}

	/**
	 * 画面表示時に使用するフォームをModelへ自動で格納する。
	 * Thymeleaf側で ${expenseForm} として参照できる。
	 *
	 * @return 初期状態のExpenseForm
	 */
	@ModelAttribute("expenseForm")
	public ExpenseForm expenseForm() {
		return new ExpenseForm();
	}

	/**
	 * 交通費申請画面（一覧）を表示する。
	 *
	 * ・未ログインの場合はログイン画面へ遷移
	 * ・ログイン中のユーザーに紐づく申請一覧を取得して画面に渡す
	 *
	 * @param model 画面表示用モデル
	 * @return 交通費申請画面
	 */
	@GetMapping("/travelCost")
	public String showTravelCost(Model model) {

		// 未ログインの場合はログイン画面へリダイレクト
		User loginUser = (User) session.getAttribute("loginUser");
		if (loginUser == null) {
			return "redirect:/login";
		}

		// ログインユーザーの申請一覧を取得し、画面に渡す
		List<Expense> expenseList = expenseService.findUserExpenses(loginUser);
		model.addAttribute("expenseList", expenseList);

		return "travelCost";
	}

	/**
	 * 交通費申請を登録する。
	 *
	 * ・未ログインの場合はログイン画面へ遷移
	 * ・入力チェックエラーがある場合は同画面へ戻す
	 * ・登録成功時はフラッシュメッセージを設定して一覧へリダイレクトする
	 *
	 * @param form   入力フォーム（バリデーション対象）
	 * @param result バリデーション結果
	 * @param ra     リダイレクト先にメッセージを渡すための属性
	 * @return 申請画面への遷移先
	 */
	@PostMapping("/travelCost")
	public String submitTravelCost(
			@Valid @ModelAttribute("expenseForm") ExpenseForm form,
			BindingResult result,
			RedirectAttributes ra) {

		// 未ログインの場合はログイン画面へリダイレクト
		User loginUser = (User) session.getAttribute("loginUser");
		if (loginUser == null) {
			return "redirect:/login";
		}

		// 入力エラーがある場合は同じ画面を再表示
		if (result.hasErrors()) {
			return "travelCost";
		}

		// 申請データを登録
		expenseService.createExpense(form, loginUser);

		// 登録完了メッセージをフラッシュ属性として設定
		ra.addFlashAttribute("message", Constants.SUBMIT_EXPENSE);

		return "redirect:/travelCost";
	}
	
	/**
	 * 差戻し（RETURNED）された申請を再提出する。
	 *
	 * ・未ログインの場合はログイン画面へ遷移
	 * ・対象申請が自分の申請でない場合は一覧へ戻す（不正操作対策）
	 * ・RETURNED の場合のみ SUBMITTED へ戻す（Service側で制御）
	 *
	 * @param id      再提出対象の申請ID
	 * @return 申請画面へのリダイレクト
	 */
	@PostMapping("/expenses/{id}/resubmit")
	public String resubmit(@PathVariable("id") Long id) {

	    // 未ログインの場合はログイン画面へリダイレクト
	    User loginUser = (User) session.getAttribute("loginUser");
	    if (loginUser == null) {
	        return "redirect:/login";
	    }

	    // 対象申請が「自分の申請」かチェック（他人の申請を触れないようにする）
	    Expense expense = expenseService.findById(id);
	    if (expense == null || !expense.getUserId().equals(loginUser.getId())) {
	        return "redirect:/travelCost";
	    }

	    // 再提出（RETURNED → SUBMITTED、returnReasonはクリア）
	    expenseService.resubmitExpense(id);

	    return "redirect:/travelCost";
	}

}
