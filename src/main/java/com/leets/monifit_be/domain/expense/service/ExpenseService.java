package com.leets.monifit_be.domain.expense.service;

import com.leets.monifit_be.domain.budget.entity.BudgetPeriod;
import com.leets.monifit_be.domain.budget.entity.PeriodStatus; // 1. PeriodStatus 임포트 추가
import com.leets.monifit_be.domain.budget.repository.BudgetPeriodRepository;
import com.leets.monifit_be.domain.expense.dto.ExpenseCreateRequest;
import com.leets.monifit_be.domain.expense.dto.ExpenseCreateResponse;
import com.leets.monifit_be.domain.expense.entity.Expense;
import com.leets.monifit_be.domain.expense.repository.ExpenseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final BudgetPeriodRepository budgetPeriodRepository;

    public ExpenseService(ExpenseRepository expenseRepository, BudgetPeriodRepository budgetPeriodRepository) {
        this.expenseRepository = expenseRepository;
        this.budgetPeriodRepository = budgetPeriodRepository;
    }

    @Transactional
    public ExpenseCreateResponse createExpense(Long memberId, ExpenseCreateRequest request) {
        // 1. 날짜 설정: 기본값 오늘
        LocalDate spentDate = (request.getSpentDate() != null) ? request.getSpentDate() : LocalDate.now();

        // 2. 현재 활성화된 예산 기간 조회 (에러 해결: PeriodStatus.ACTIVE 인자 추가)
        // 요구사항 4-2: 지출 입력 시 현재 활성화된 예산 기간에 귀속되어야 함
        BudgetPeriod budgetPeriod = budgetPeriodRepository.findByMemberIdAndStatus(memberId, PeriodStatus.ACTIVE)
                .orElseThrow(() -> new IllegalArgumentException("활성화된 예산 기간이 없습니다."));

        // 3. 지출 엔티티 생성 및 저장 (Builder 패턴 사용)
        Expense expense = Expense.builder()
                .budgetPeriod(budgetPeriod)
                .category(request.getCategory())
                .amount(request.getAmount())
                .spentDate(spentDate)
                .build();

        expenseRepository.save(expense);

        // 4. 예산 초과 및 자동 종료 로직
        long totalAmount = expenseRepository.sumAmountByBudgetPeriod(budgetPeriod);

        // 엔티티의 필드명이 getBudgetAmount()인지 getTargetBudget()인지 확인 필요 (여기서는 budgetAmount 기준)
        boolean isOverBudget = totalAmount > budgetPeriod.getBudgetAmount();

        if (isOverBudget) {
            // 요구사항 7-2: 예산 초과 시 현재 활성화된 기간은 즉시 강제 종료(완료 처리)
            budgetPeriod.complete(com.leets.monifit_be.domain.budget.entity.CompletionType.OVER_BUDGET);
        }

        String message = isOverBudget ?
                "예산을 초과하여 기간이 종료되었습니다. 재설정이 필요해요." :
                "지출이 기록되었습니다! 오늘 스탬프를 확인하세요.";

        return new ExpenseCreateResponse(
                expense.getId(),
                isOverBudget,
                isOverBudget ? "OVER_BUDGET" : null,
                message
        );
    }
}