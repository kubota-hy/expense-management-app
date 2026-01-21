package com.example.demo.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.entity.Expense;
import com.example.demo.entity.User;
import com.example.demo.form.AdminExpenseSearchForm;
import com.example.demo.form.ExpenseForm;
import com.example.demo.repository.ExpenseRepository;
import com.example.demo.rules.Decision;
import com.example.demo.rules.RuleEngine;
import com.example.demo.util.Constants;
import com.example.demo.util.Statuses;

/**
 * 交通費申請に関する業務処理を提供するサービスクラス。
 * 申請の登録、申請一覧の取得、承認・却下などの処理を担当する。
 *
 * @author Kubota
 */
@Service
public class ExpenseService {

	/** 交通費申請データへアクセスするリポジトリ */
	private final ExpenseRepository expenseRepository;
	
	private final  RuleEngine ruleEngine;

	/**
	 * コンストラクタインジェクションによりリポジトリを受け取る。
	 *
	 * @param expenseRepository 交通費申請リポジトリ
	 */
	public ExpenseService(RuleEngine ruleEngine,ExpenseRepository expenseRepository) {
		this.expenseRepository = expenseRepository;
		this.ruleEngine = ruleEngine;
	}

	/**
	 * 交通費申請を新規登録する。
	 *
	 * 入力フォームの内容とログインユーザー情報から申請データを作成し、
	 * DBへ保存する。
	 *
	 * @param form      申請入力フォーム
	 * @param loginUser ログインユーザー
	 */
	public void createExpense(ExpenseForm form, User loginUser) {

		Expense expense = new Expense();

		// ログインユーザーに紐づく申請として登録
		expense.setUserId(loginUser.getId());

		// 入力値を申請エンティティへ詰め替え
		expense.setUseDate(form.getUseDate());
		expense.setClaimType(form.getClaimType());

		expense.setTransportation(form.getTransportation());
		expense.setFromPlace(form.getFromPlace());
		expense.setToPlace(form.getToPlace());
		expense.setTripType(form.getTripType());

		expense.setAmount(form.getAmount());
		expense.setPurpose(form.getPurpose());
		expense.setNote(form.getNote());
		
		Decision decision = ruleEngine.decide(expense);

		switch (decision) {
		    case APPROVE -> expense.setStatus("APPROVED");
		    case NEED_REVIEW -> expense.setStatus("SUBMITTED"); // 要確認=申請中扱いにする（ミニ版）
		    case REJECT -> expense.setStatus("REJECTED");       // 新しいステータス（Stringなので追加するだけでOK）
		}
		expenseRepository.save(expense);
	}

	/**
	 * 指定ユーザーの申請一覧を取得する（使用日降順）。
	 *
	 * @param user 対象ユーザー
	 * @return 対象ユーザーの申請一覧
	 */
	public List<Expense> findUserExpenses(User user) {
		return expenseRepository.findByUserIdOrderByUseDateDesc(user.getId());
	}

	/**
	 * 全ユーザーの申請一覧を取得する（申請日時降順）。
	 * 管理者画面での一覧表示を想定。
	 *
	 * @return 全申請一覧
	 */
	public List<Expense> findAllExpenses() {
		return expenseRepository.findAllByOrderBySubmittedAtDesc();
	}

	/**
	 * 指定された申請を承認する。
	 *
	 * ・申請が存在しない場合は例外を送出する
	 * ・ステータスがSUBMITTED（申請中）の場合のみ、APPROVEDへ更新する
	 *
	 * @param expenseId 承認対象の申請ID
	 * @throws IllegalArgumentException 申請が存在しない場合
	 */
	public void approveExpenses(Long expenseId) {
		Expense expense = expenseRepository.findById(expenseId)
				.orElseThrow(() -> new IllegalArgumentException(Constants.ERROR02));

		// SUBMITTEDステータスのみ変更対象とする
		if (!Statuses.SUBMITTED.equals(expense.getStatus())) {
			return;
		}

		expense.setStatus(Statuses.APPROVED);
		expenseRepository.save(expense);
	}

	/**
	 * 指定された申請を却下する。
	 *
	 * ・申請が存在しない場合は例外を送出する
	 * ・ステータスがSUBMITTED（申請中）の場合のみ、REJECTEDへ更新する
	 *
	 * @param expenseId 却下対象の申請ID
	 * @throws IllegalArgumentException 申請が存在しない場合
	 */
	public void rejectExpenses(Long expenseId) {
		Expense expense = expenseRepository.findById(expenseId)
				.orElseThrow(() -> new IllegalArgumentException(Constants.ERROR02));

		// SUBMITTEDステータスのみ変更対象とする
		if (!Statuses.SUBMITTED.equals(expense.getStatus())) {
			return;
		}

		expense.setStatus(Statuses.REJECTED);
		expenseRepository.save(expense);
	}

	/**
	 * 管理者向けに交通費申請を検索する。
	 *
	 * 画面から受け取った検索条件を正規化・変換した上で、
	 * Repository に検索処理を委譲する。
	 *
	 * ・未入力項目は検索条件から除外される
	 * ・申請者名は部分一致検索
	 * ・日付は「開始日 00:00 以上」「終了日の翌日 00:00 未満」で判定する
	 *
	 * @param form 管理者用交通費申請検索フォーム
	 * @return 検索条件に一致する交通費申請一覧
	 */
	public List<Expense> searchExpensesForAdmin(AdminExpenseSearchForm form){

		String name = normalize(form.getUserName());
	    String status = normalize(form.getStatus());
	    String claimType = normalize(form.getClaimType());

	    LocalDateTime from = parseStartOfDay(form.getSubmittedFrom());
	    LocalDateTime to   = parseEndExclusive(form.getSubmittedTo()); // toは翌日00:00

	    // ★ nullを絶対に渡さない（ここが肝）
	    if (from == null) {
	        from = LocalDateTime.of(1900, 1, 1, 0, 0);
	    }
	    if (to == null) {
	        to = LocalDateTime.of(2999, 12, 31, 0, 0);
	    }

	    return expenseRepository.searchForAdmin(
	        name, status, claimType,
	        from, to
	    );

	}

	/**
	 * 文字列の検索条件を正規化する。
	 *
	 * ・null の場合はそのまま null を返す
	 * ・前後の空白を除去する
	 * ・空文字（""）や空白のみの場合は null を返す
	 *
	 * Repository の動的検索条件で
	 * 「条件未指定（null）」として扱うための前処理。
	 *
	 * @param s 入力文字列
	 * @return 正規化された文字列、または null
	 */
	private String normalize(String s) {
		
		if(s == null) return null;
		
		String t = s.trim();
		
		return t.isEmpty() ? null: t;
		
	}

	/**
	 * 日付文字列（yyyy-MM-dd）をその日の開始時刻（00:00）に変換する。
	 *
	 * 開始日未入力の場合は null を返し、
	 * 検索条件としては「下限なし」として扱う。
	 *
	 * @param yyyyMmDd 日付文字列（yyyy-MM-dd）
	 * @return LocalDateTime（当日 00:00）、または null
	 */
	private LocalDateTime parseStartOfDay(String yyyyMmDd) {
		
		if (yyyyMmDd == null || yyyyMmDd.isBlank()) return null;
		
		return LocalDate.parse(yyyyMmDd).atStartOfDay();
	}

	/**
	 * 日付文字列（yyyy-MM-dd）を検索用の終了境界に変換する。
	 *
	 * 指定日の翌日 00:00 を返すことで、
	 * 「指定日を含む」検索条件を
	 * 「終了日時 < 翌日00:00」という形で実現する。
	 *
	 * 終了日未入力の場合は null を返し、
	 * 検索条件としては「上限なし」として扱う。
	 *
	 * @param yyyyMmDd 日付文字列（yyyy-MM-dd）
	 * @return LocalDateTime（翌日 00:00）、または null
	 */
	private LocalDateTime parseEndExclusive(String yyyyMmDd) {
		if (yyyyMmDd == null || yyyyMmDd.isBlank()) return null;
		return LocalDate.parse(yyyyMmDd).plusDays(1).atStartOfDay();
	}
	/**
	 * 指定された申請を差戻しする。
	 *
	 * ・申請が存在しない場合は例外
	 * ・ステータスがSUBMITTEDの場合のみ、RETURNEDへ更新する
	 * ・差戻し理由を保存する
	 *
	 * @param expenseId 差戻し対象の申請ID
	 * @param reason 差戻し理由（空の場合はデフォルト文言を設定）
	 */
	public void returnExpense(Long expenseId,String reason) {
		
		Expense expense = expenseRepository.findById(expenseId)
	            .orElseThrow(() -> new IllegalArgumentException(Constants.ERROR02));
		
		if(!Statuses.SUBMITTED.equals(expense.getStatus())) {
			return;
		}
		
		String r = (reason == null || reason.trim().isEmpty())
				? Constants.RETURN_SIBMITT
						: reason.trim();
		
		expense.setStatus(Statuses.RETURNED);
		expense.setReturnReason(r);
		expenseRepository.save(expense);
	}
	
	/**
	 * 指定された差戻し申請を再提出する。
	 *
	 * ・申請が存在しない場合は例外
	 * ・ステータスがRETURNEDの場合のみ、SUBMITTEDへ戻す
	 * ・差戻し理由はクリアする
	 *
	 * @param expenseId 再提出対象の申請ID
	 * @throws IllegalArgumentException 申請が存在しない場合
	 */
	public void resubmitExpense(Long expenseId) {
	    Expense expense = expenseRepository.findById(expenseId)
	            .orElseThrow(() -> new IllegalArgumentException(Constants.ERROR02));

	    // RETURNED のみ再提出対象
	    if (!Statuses.RETURNED.equals(expense.getStatus())) {
	        return;
	    }

	    expense.setStatus(Statuses.SUBMITTED);
	    expense.setReturnReason(null);
	    expenseRepository.save(expense);
	}


}