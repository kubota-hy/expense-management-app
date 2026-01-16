package com.example.demo.form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminUserCreateForm {
	
	@NotBlank(message = "氏名は必須です。")
	@Size(max = 50,message = "氏名は50文字以内で入力して下さい。")
	private String name;
	
	@NotBlank(message = "メールアドレスは必須です。")
    @Email(message = "メールアドレスの形式が正しくありません。")
    @Size(max = 255, message = "メールアドレスは255文字以内で入力してください。")
    private String email;
	
    @NotBlank(message = "初期パスワードは必須です。")
    @Size(min = 8, max = 72, message = "パスワードは8〜72文字で入力してください。")
    private String password;

    @NotBlank(message = "確認用パスワードは必須です。")
    private String passwordConfirm;


}
