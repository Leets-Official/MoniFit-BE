package com.leets.monifit_be.domain.expense.service;

import com.leets.monifit_be.domain.budget.entity.BudgetPeriod;
import com.leets.monifit_be.domain.budget.entity.CompletionType;
import com.leets.monifit_be.domain.budget.entity.PeriodStatus;
import com.leets.monifit_be.domain.budget.repository.BudgetPeriodRepository;
import com.leets.monifit_be.domain.expense.dto.ExpenseCreateRequest;
import com.leets.monifit_be.domain.expense.dto.ExpenseCreateResponse;
import com.leets.monifit_be.domain.expense.entity.Expense;
import com.leets.monifit_be.domain.expense.repository.ExpenseRepository;
import com.leets.monifit_be.global.exception.ActiveBudgetNotFoundException; // 커스텀 예외
import lombok.RequiredArgsConstructor; // 롬복 추가
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final BudgetPeriodRepository budgetPeriodRepository;

    @Transactional
    public ExpenseCreateResponse createExpense(Long memberId, ExpenseCreateRequest request) {
        // 날짜 설정: 기본값 오늘
        LocalDate spentDate = (request.getSpentDate() != null) ? request.getSpentDate() : LocalDate.now();

        // 현재 활성화된 예산 기간 조회
        BudgetPeriod budgetPeriod = budgetPeriodRepository.findByMemberIdAndStatus(memberId, PeriodStatus.ACTIVE)
                .orElseThrow(() -> new ActiveBudgetNotFoundException("활성화된 예산 기간이 없습니다."));

        // 오늘 첫 지출 여부 확인
        boolean todayFirstExpense = !expenseRepository.existsByBudgetPeriodAndSpentDate(budgetPeriod, spentDate);

        // 지출 엔티티 생성 및 저장
        Expense expense = Expense.builder()
                .budgetPeriod(budgetPeriod)
                .category(request.getCategory())
                .amount(request.getAmount())
                .spentDate(spentDate)
                .build();

        expenseRepository.save(expense);

        // 예산 초과 및 자동 종료 로직
        long totalAmount = expenseRepository.sumAmountByBudgetPeriod(budgetPeriod);
        boolean isOverBudget = totalAmount > budgetPeriod.getBudgetAmount();

        if (isOverBudget) {
            budgetPeriod.complete(CompletionType.OVER_BUDGET);
        }

        String message = isOverBudget ?
                "예산을 초과하여 기간이 종료되었습니다. 재설정이 필요해요." :
                "지출이 기록되었습니다! 오늘 스탬프를 확인하세요.";

        // DTO 생성
        return new ExpenseCreateResponse(
                expense.getId(),
                isOverBudget,
                isOverBudget ? "OVER_BUDGET" : null,
                message,
                todayFirstExpense
        );
    }
}