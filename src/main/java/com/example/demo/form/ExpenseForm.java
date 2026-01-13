package com.example.demo.form;

import java.time.LocalDate;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExpenseForm {

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate useDate; // 利用日

    @NotBlank
    private String claimType;  // 請求区分（交通費/その他）

    @NotBlank
    private String transportation; // 交通手段（電車/バス/タクシー等）

    @NotBlank
    @Size(max = 100)
    private String fromPlace; // 出発地

    @NotBlank
    @Size(max = 100)
    private String toPlace;   // 到着地

    @NotBlank
    private String tripType;  // 片道/往復

    @NotNull
    @Min(0)
    private Integer amount;   // 金額

    @NotBlank
    @Size(max = 200) // Entity側が200なので合わせる
    private String purpose;   // 請求目的

    @Size(max = 500)
    private String note;      // 備考（任意）
}
