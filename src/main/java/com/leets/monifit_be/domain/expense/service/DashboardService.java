package com.leets.monifit_be.domain.expense.service;

import com.leets.monifit_be.domain.expense.dto.DashboardResponse;
import com.leets.monifit_be.domain.budget.entity.BudgetPeriod;
import com.leets.monifit_be.domain.budget.entity.PeriodStatus;
import com.leets.monifit_be.domain.budget.entity.CompletionType;
import com.leets.monifit_be.domain.budget.repository.BudgetPeriodRepository;
import com.leets.monifit_be.domain.expense.repository.ExpenseRepository;
//import com.leets.monifit_be.global.exception.ActiveBudgetNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final BudgetPeriodRepository budgetPeriodRepository;
    private final ExpenseRepository expenseRepository;

    @Transactional
    public DashboardResponse getDashboardData(Long memberId) {

        // 활성 기간 조회 (없으면 null 처리를 위해 orElse 사용 검토 필요하나 명세서상 404 처리)
        BudgetPeriod period = budgetPeriodRepository.findByMemberIdAndStatus(memberId, PeriodStatus.ACTIVE)
                .orElse(null);

        if (period == null) {
            return DashboardResponse.builder()
                    .hasPeriod(false)
                    .period(null)
                    .alerts(DashboardResponse.DashboardAlerts.builder().build())
                    .build();
        }

        // 기본 데이터 계산
        long totalBudget = period.getBudgetAmount();
        long totalExpense = expenseRepository.sumAmountByBudgetPeriod(period);
        long remainingBudget = Math.max(0, totalBudget - totalExpense);
        double usageRate = (totalBudget > 0) ? (double) totalExpense / totalBudget * 100 : 0;

        long totalDays = ChronoUnit.DAYS.between(period.getStartDate(), period.getEndDate()) + 1;
        long elapsedDays = ChronoUnit.DAYS.between(period.getStartDate(), LocalDate.now()) + 1;
        long remainingDays = ChronoUnit.DAYS.between(LocalDate.now(), period.getEndDate());
        double progressRate = ((double) Math.min(elapsedDays, totalDays) / totalDays) * 100;
        long dailyRecommended = (remainingDays > 0) ? remainingBudget / remainingDays : 0;

        // 3. 50% 경고 알림 로직
        boolean showWarning = false;
        DashboardResponse.WarningDetail warningDetail = null;
        if (usageRate >= 50.0 && !period.isWarningShown()) {
            showWarning = true;
            period.setWarningShown(true); // 조회 시 자동으로 플래그 업데이트
            warningDetail = DashboardResponse.WarningDetail.builder()
                    .title("예산의 50%를 초과했어요 ⚠")
                    .message("남은 기간 동안 하루 ₩" + dailyRecommended + "으로 조절하면 목표 달성이 가능해요")
                    .dailyRecommendedExpense((int) dailyRecommended)
                    .build();
        }

        // 마감일 도달 처리
        boolean showPeriodComplete = false;
        if (LocalDate.now().isAfter(period.getEndDate())) {
            period.setStatus(PeriodStatus.COMPLETED);
            period.setCompletionType(CompletionType.SUCCESS);
            showPeriodComplete = true;
        }

        // 5. 응답 조립 (Builder 사용)
        return DashboardResponse.builder()
                .hasPeriod(true)
                .period(DashboardResponse.ActivePeriodDto.builder()
                        .id(period.getId())
                        .startDate(period.getStartDate())
                        .endDate(period.getEndDate())
                        .budgetAmount((int) totalBudget)
                        .totalExpense((int) totalExpense)
                        .remainingBudget((int) remainingBudget)
                        .usageRate(usageRate)
                        .totalDays((int) totalDays)
                        .remainingDays((int) remainingDays)
                        .dailyRecommendedExpense((int) dailyRecommended)
                        .build())
                .alerts(DashboardResponse.DashboardAlerts.builder()
                        .showWarning(showWarning)
                        .warning(warningDetail)
                        .showPeriodComplete(showPeriodComplete)
                        .build())
                .build();
    }
}