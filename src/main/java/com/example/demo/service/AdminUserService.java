package com.example.demo.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.User;
import com.example.demo.form.AdminUserCreateForm;
import com.example.demo.repository.UserRepository;
import com.example.demo.util.Constants;
import com.example.demo.util.PasswordUtil;

@Service
public class AdminUserService {

	private final UserRepository userRepository;

	public AdminUserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	/**
	 * 管理者がユーザー（USER権限）を新規登録する。
	 *
	 * @param form 入力フォーム
	 * @throws IllegalArgumentException 入力不正（パスワード不一致、メール重複など）
	 */
	@Transactional
	public void createUserByAdmin(AdminUserCreateForm form) {

		//パスワード一致チェック
		if(!form.getPassword().equals(form.getPasswordConfirm())) {

			throw new IllegalArgumentException(Constants.ERROR03);

		}

		//email重複チェック
		if(userRepository.existsByEmail(form.getEmail())) {

			throw new IllegalArgumentException(Constants.ERROR04);

		}

		//登録(roleはUSER固定)
		User user = new User();
		user.setName(form.getName());
		user.setEmail(form.getEmail());
		user.setPasswordHash(PasswordUtil.hash(form.getPassword()));
		user.setRole("USER");
		user.setEnabled(true);
		
		userRepository.save(user);
	}


}