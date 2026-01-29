package com.leets.monifit_be.domain.expense.service;

import com.leets.monifit_be.domain.expense.dto.DashboardResponse;
import com.leets.monifit_be.domain.budget.entity.BudgetPeriod;
import com.leets.monifit_be.domain.budget.entity.PeriodStatus;
import com.leets.monifit_be.domain.budget.entity.CompletionType;
import com.leets.monifit_be.domain.budget.repository.BudgetPeriodRepository;
import com.leets.monifit_be.domain.expense.repository.ExpenseRepository;
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

                // 1. 활성 기간 조회
                BudgetPeriod period = budgetPeriodRepository.findByMemberIdAndStatus(memberId, PeriodStatus.ACTIVE)
                                .orElse(null);

                // 2. 활성 기간 없음 → 완료된 기간 중 알림 표시 필요한지 확인
                if (period == null) {
                        return handleNoPeriod(memberId);
                }

                // 3. 기본 데이터 계산
                int budgetAmount = period.getBudgetAmount();
                long totalExpense = expenseRepository.sumAmountByBudgetPeriod(period);
                long remainingBudget = Math.max(0, budgetAmount - totalExpense);
                double usageRate = (budgetAmount > 0) ? (double) totalExpense / budgetAmount * 100 : 0;
                double savingRate = Math.max(0, 100 - usageRate);

                long totalDays = ChronoUnit.DAYS.between(period.getStartDate(), period.getEndDate()) + 1;
                long elapsedDays = Math.min(ChronoUnit.DAYS.between(period.getStartDate(), LocalDate.now()) + 1,
                                totalDays);
                long remainingDays = Math.max(0, ChronoUnit.DAYS.between(LocalDate.now(), period.getEndDate()));
                double progressRate = ((double) elapsedDays / totalDays) * 100;
                long dailyRecommended = (remainingDays > 0) ? remainingBudget / remainingDays : 0;

                // 절약/초과 금액 계산
                Integer savedAmount = null;
                Integer exceededAmount = null;
                if (totalExpense <= budgetAmount) {
                        savedAmount = (int) (budgetAmount - totalExpense);
                } else {
                        exceededAmount = (int) (totalExpense - budgetAmount);
                }

                // 4. 마감일 도달 체크 및 처리
                boolean showPeriodComplete = false;
                DashboardResponse.PeriodCompleteDetail periodCompleteDetail = null;
                if (LocalDate.now().isAfter(period.getEndDate())) {
                        // 예산 초과 여부에 따라 completionType 결정
                        CompletionType completionType = totalExpense <= budgetAmount
                                        ? CompletionType.SUCCESS
                                        : CompletionType.OVER_BUDGET;
                        period.complete(completionType);

                        if (!period.getPeriodCompleteShown()) {
                                showPeriodComplete = true;
                                period.showPeriodComplete();

                                if (completionType == CompletionType.SUCCESS) {
                                        periodCompleteDetail = DashboardResponse.PeriodCompleteDetail.builder()
                                                        .title("기간 종료! 🎉")
                                                        .message1("이번 기간 동안 예산을 잘 관리했어요")
                                                        .message2("총 ₩" + String.format("%,d",
                                                                        savedAmount != null ? savedAmount : 0)
                                                                        + "을 절약했습니다")
                                                        .savedAmount(savedAmount)
                                                        .build();
                                } else {
                                        // 예산 초과 상태로 기간 종료
                                        periodCompleteDetail = DashboardResponse.PeriodCompleteDetail.builder()
                                                        .title("기간 종료")
                                                        .message1("이번 기간은 예산을 초과했어요")
                                                        .message2("₩" + String.format("%,d",
                                                                        exceededAmount != null ? exceededAmount : 0)
                                                                        + " 초과했습니다")
                                                        .savedAmount(0)
                                                        .build();
                                }
                        }

                        // 기간 종료 시 hasPeriod = false
                        return DashboardResponse.builder()
                                        .hasPeriod(false)
                                        .period(null)
                                        .alerts(DashboardResponse.DashboardAlerts.builder()
                                                        .showWarning(false)
                                                        .showOverBudget(false)
                                                        .showPeriodComplete(showPeriodComplete)
                                                        .warning(null)
                                                        .overBudget(null)
                                                        .periodComplete(periodCompleteDetail)
                                                        .build())
                                        .build();
                }

                // 5. 예산 초과 체크
                boolean showOverBudget = false;
                DashboardResponse.OverBudgetDetail overBudgetDetail = null;
                if (totalExpense > budgetAmount) {
                        period.complete(CompletionType.OVER_BUDGET);
                        if (!period.getOverBudgetShown()) {
                                showOverBudget = true;
                                period.showOverBudget();
                                overBudgetDetail = DashboardResponse.OverBudgetDetail.builder()
                                                .title("예산을 ₩" + String.format("%,d", exceededAmount) + " 초과했어요 ⚠")
                                                .message("다음 기간엔 소비 속도를 조절해봐요")
                                                .exceededAmount(exceededAmount)
                                                .build();
                        }

                        // 예산 초과 시 hasPeriod = false
                        return DashboardResponse.builder()
                                        .hasPeriod(false)
                                        .period(null)
                                        .alerts(DashboardResponse.DashboardAlerts.builder()
                                                        .showWarning(false)
                                                        .showOverBudget(showOverBudget)
                                                        .showPeriodComplete(false)
                                                        .warning(null)
                                                        .overBudget(overBudgetDetail)
                                                        .periodComplete(null)
                                                        .build())
                                        .build();
                }

                // 6. 50% 경고 알림 로직
                boolean showWarning = false;
                DashboardResponse.WarningDetail warningDetail = null;
                if (usageRate >= 50.0 && !period.getWarningShown()) {
                        showWarning = true;
                        period.showWarning();
                        warningDetail = DashboardResponse.WarningDetail.builder()
                                        .title("예산의 50%를 초과했어요 ⚠")
                                        .message("남은 기간 동안 하루 ₩" + String.format("%,d", dailyRecommended)
                                                        + "으로 조절하면 목표 달성이 가능해요")
                                        .dailyRecommendedExpense((int) dailyRecommended)
                                        .build();
                }

                // 7. 응답 조립 (명세서와 100% 일치)
                return DashboardResponse.builder()
                                .hasPeriod(true)
                                .period(DashboardResponse.ActivePeriodDto.builder()
                                                .id(period.getId())
                                                .startDate(period.getStartDate())
                                                .endDate(period.getEndDate())
                                                .budgetAmount(budgetAmount)
                                                .totalExpense((int) totalExpense)
                                                .remainingBudget((int) remainingBudget)
                                                .savedAmount(savedAmount)
                                                .exceededAmount(exceededAmount)
                                                .usageRate(Math.round(usageRate * 10.0) / 10.0)
                                                .savingRate(Math.round(savingRate * 10.0) / 10.0)
                                                .totalDays((int) totalDays)
                                                .elapsedDays((int) elapsedDays)
                                                .remainingDays((int) remainingDays)
                                                .progressRate(Math.round(progressRate * 10.0) / 10.0)
                                                .dailyRecommendedExpense((int) dailyRecommended)
                                                .build())
                                .alerts(DashboardResponse.DashboardAlerts.builder()
                                                .showWarning(showWarning)
                                                .showOverBudget(false)
                                                .showPeriodComplete(false)
                                                .warning(warningDetail)
                                                .overBudget(null)
                                                .periodComplete(null)
                                                .build())
                                .build();
        }

        /**
         * 활성 기간 없을 때 처리
         * - 최근 완료된 기간의 알림 표시 여부 확인
         */
        private DashboardResponse handleNoPeriod(Long memberId) {
                // 최근 완료된 기간 조회하여 미표시 알림 확인 (추후 구현 가능)
                return DashboardResponse.builder()
                                .hasPeriod(false)
                                .period(null)
                                .alerts(DashboardResponse.DashboardAlerts.builder()
                                                .showWarning(false)
                                                .showOverBudget(false)
                                                .showPeriodComplete(false)
                                                .warning(null)
                                                .overBudget(null)
                                                .periodComplete(null)
                                                .build())
                                .build();
        }
}