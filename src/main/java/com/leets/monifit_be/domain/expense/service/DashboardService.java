package com.leets.monifit_be.domain.expense.service;

import com.leets.monifit_be.domain.expense.dto.DashboardResponse;
import com.leets.monifit_be.domain.budget.entity.BudgetPeriod;
import com.leets.monifit_be.domain.budget.entity.PeriodStatus;
import com.leets.monifit_be.domain.budget.repository.BudgetPeriodRepository;
import com.leets.monifit_be.domain.expense.repository.ExpenseRepository;
import com.leets.monifit_be.global.exception.ActiveBudgetNotFoundException; // 커스텀 예외 임포트
import lombok.RequiredArgsConstructor; // 롬복 임포트
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor // 1. Lombok으로 생성자 자동 생성
public class DashboardService {

    private final BudgetPeriodRepository budgetPeriodRepository;
    private final ExpenseRepository expenseRepository;

    // 2. 기존 생성자(public DashboardService...) 삭제 완료

    @Transactional(readOnly = true)
    public DashboardResponse getDashboardData(Long memberId) {

        // 3. 커스텀 예외(ActiveBudgetNotFoundException) 사용
        BudgetPeriod period = budgetPeriodRepository.findByMemberIdAndStatus(memberId, PeriodStatus.ACTIVE)
                .orElseThrow(() -> new ActiveBudgetNotFoundException("진행 중인 예산이 없습니다."));

        long totalBudget = period.getBudgetAmount();
        long totalExpense = expenseRepository.sumAmountByBudgetPeriod(period);
        long remainingBudget = Math.max(0, totalBudget - totalExpense);

        double usageRate = (totalBudget > 0) ? (double) totalExpense / totalBudget * 100 : 0;

        long remainingDays = ChronoUnit.DAYS.between(LocalDate.now(), period.getEndDate());
        if (remainingDays < 0) remainingDays = 0;

        long dailyRecommended = (remainingDays > 0) ? remainingBudget / remainingDays : 0;

        return new DashboardResponse(
                remainingBudget,
                totalExpense,
                usageRate,
                (int) remainingDays,
                dailyRecommended,
                usageRate >= 50.0,
                totalExpense > totalBudget,
                remainingDays <= 0
        );
    }
}