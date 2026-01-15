package com.example.demo.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * ログアウト処理を担当するコントローラクラス。
 * セッションを無効化し、ログイン画面へ遷移する。
 *
 * @author Kubota
 */
@Controller
public class LogoutController {

	/**
	 * ログアウト処理を行う。
	 *
	 * セッションを破棄することでログイン状態を解除し、
	 * ログイン画面を再表示する。
	 *
	 * @param session セッション情報
	 * @return ログイン画面
	 */
	@GetMapping("/logout")
	public String logout(HttpSession session) {

		// セッションを無効化してログアウトする
		session.invalidate();

		return "login";
	}
}
