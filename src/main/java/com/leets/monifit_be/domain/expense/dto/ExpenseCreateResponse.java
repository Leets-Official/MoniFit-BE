package com.leets.monifit_be.domain.expense.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor // 모든 필드를 인자로 받는 생성자 자동 생성
@NoArgsConstructor  // 기본 생성자 자동 생성 (JSON 직렬화/역직렬화 시 필요할 수 있음)
public class ExpenseCreateResponse {

    private Long expenseId;

    private boolean periodCompleted;

    private String completionType;

    private String message;

    private boolean todayFirstExpense;
}