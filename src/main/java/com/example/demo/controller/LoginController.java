package com.example.demo.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import com.example.demo.util.Constants;

/**
 * ログイン機能を担当するコントローラクラス。
 * ユーザー認証を行い、権限（管理者／一般）に応じて
 * 遷移先画面を振り分ける。
 *
 * @author Kubota
 */
@Controller
public class LoginController {

	/** ユーザー認証処理を行うサービス */
	private final UserService userService;

	/**
	 * コンストラクタインジェクションによりサービスを受け取る。
	 *
	 * @param userService ユーザーサービス
	 */
	public LoginController(UserService userService) {
		this.userService = userService;
	}

	/**
	 * ログイン画面を表示する。
	 *
	 * @return ログイン画面
	 */
	@GetMapping("/login")
	public String showLogin() {
		return "login";
	}

	/**
	 * ログイン認証を行う。
	 *
	 * ・メールアドレスとパスワードで認証を実施
	 * ・認証失敗時はエラーメッセージを表示してログイン画面へ戻す
	 * ・認証成功時はセッションにログイン情報を保持し、
	 *   権限に応じて遷移先を振り分ける
	 *
	 * @param email    入力されたメールアドレス
	 * @param password 入力されたパスワード
	 * @param session  セッション情報
	 * @param model    画面表示用モデル
	 * @return 遷移先画面
	 */
	@PostMapping("/login")
	public String login(
			@RequestParam String email,
			@RequestParam String password,
			HttpSession session,
			Model model) {

		// ユーザー認証を実施
		User user = userService.login(email, password);

		// 認証失敗時はエラーメッセージを表示してログイン画面へ戻す
		if (user == null) {
			model.addAttribute("error", Constants.ERROR01);
			return "login";
		}

		// 認証成功時はセッションにログインユーザー情報を保持
		session.setAttribute("loginUser", user);

		// 権限に応じて遷移先を振り分け
		if ("ADMIN".equals(user.getRole())) {
			return "redirect:/top";
		} else {
			return "redirect:/travelCost";
		}
	}
}
