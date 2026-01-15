package com.example.demo.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;

/**
 * ユーザーに関する業務処理を提供するサービスクラス。
 * 主にログイン認証（メールアドレス・パスワードの照合、利用可否の確認）を担当する。
 *
 * @author Kubota
 */
@Service
public class UserService {

	/** ユーザー情報へアクセスするリポジトリ */
	private final UserRepository userRepository;

	/**
	 * コンストラクタインジェクションによりリポジトリを受け取る。
	 *
	 * @param userRepository ユーザーリポジトリ
	 */
	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	/**
	 * ログイン認証を行う。
	 *
	 * 以下の条件を満たす場合にユーザー情報を返す：
	 * ・メールアドレスに紐づくユーザーが存在する
	 * ・パスワードが一致する
	 * ・アカウントが有効（enabled）である
	 *
	 * 認証に失敗した場合は null を返す。
	 *
	 * @param email    メールアドレス
	 * @param password パスワード（入力値）
	 * @return 認証成功時はUser、失敗時はnull
	 */
	public User login(String email, String password) {

		// メールアドレスからユーザーを取得
		Optional<User> optUser = userRepository.findByEmail(email);

		// ユーザーが存在しない場合は認証失敗
		if (optUser.isEmpty()) {
			return null;
		}

		User user = optUser.get();

		// パスワードが一致しない場合は認証失敗
		// ※ 本来はハッシュ化した値で照合する想定（現状は簡易実装）
		if (!user.getPasswordHash().equals(password)) {
			return null;
		}

		// アカウントが無効の場合は認証失敗
		if (!user.isEnabled()) {
			return null;
		}

		return user;
	}
}
