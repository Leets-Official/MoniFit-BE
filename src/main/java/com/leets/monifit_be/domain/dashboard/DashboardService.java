package com.leets.monifit_be.domain.budget.service;

import com.leets.monifit_be.domain.budget.dto.response.DashboardResponse;
import com.leets.monifit_be.domain.budget.entity.BudgetPeriod;
import com.leets.monifit_be.domain.budget.entity.PeriodStatus; // 1. PeriodStatus 임포트 추가
import com.leets.monifit_be.domain.budget.repository.BudgetPeriodRepository;
import com.leets.monifit_be.domain.expense.repository.ExpenseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class DashboardService {

    private final BudgetPeriodRepository budgetPeriodRepository;
    private final ExpenseRepository expenseRepository;

    public DashboardService(BudgetPeriodRepository budgetPeriodRepository, ExpenseRepository expenseRepository) {
        this.budgetPeriodRepository = budgetPeriodRepository;
        this.expenseRepository = expenseRepository;
    }

    @Transactional(readOnly = true)
    public DashboardResponse getDashboardData(Long memberId) {
        // 1. 현재 활성 기간 조회 (에러 해결: PeriodStatus.ACTIVE 추가)
        // 요구사항 1-2: 설정 정보(활성 예산)가 존재할 경우 메인 화면으로 이동함
        BudgetPeriod period = budgetPeriodRepository.findByMemberIdAndStatus(memberId, PeriodStatus.ACTIVE)
                .orElseThrow(() -> new IllegalArgumentException("진행 중인 예산이 없습니다."));

        // 2. 데이터 집계
        // BudgetPeriod의 예산 필드명이 budgetAmount인지 targetBudget인지 확인 필요 (여기서는 budgetAmount로 가정)
        long totalBudget = period.getBudgetAmount();
        long totalExpense = expenseRepository.sumAmountByBudgetPeriod(period);
        long remainingBudget = Math.max(0, totalBudget - totalExpense);

        // 사용률 계산
        double usageRate = (totalBudget > 0) ? (double) totalExpense / totalBudget * 100 : 0;

        // 3. 날짜 및 권장 지출액 계산
        // 요구사항 7-1: 남은 예산을 남은 일수로 나눈 일일 권장 지출 금액 계산
        long remainingDays = ChronoUnit.DAYS.between(LocalDate.now(), period.getEndDate());
        if (remainingDays < 0) remainingDays = 0;

        long dailyRecommended = (remainingDays > 0) ? remainingBudget / remainingDays : 0;

        return new DashboardResponse(
                remainingBudget,
                totalExpense,
                usageRate,
                (int) remainingDays,
                dailyRecommended,
                usageRate >= 50.0,  // 요구사항 7-1: 50% 초과 시 경고 알림
                totalExpense > totalBudget,         // 요구사항 7-2: 예산 초과 여부
                remainingDays <= 0                  // 요구사항 7-3: 마감일 도달 여부
        );
    }
}