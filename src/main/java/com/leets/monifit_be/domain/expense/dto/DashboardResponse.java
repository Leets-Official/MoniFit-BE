package com.leets.monifit_be.domain.expense.dto;

import lombok.*;
import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DashboardResponse {
    private boolean hasPeriod;         // 활성 기간 존재 여부
    private ActivePeriodDto period;    // 활성 기간 상세 정보 (없으면 null)
    private DashboardAlerts alerts;    // 알림 정보 (경고, 초과, 종료 등)

    @Getter
    @Builder
    public static class ActivePeriodDto {
        private Long id;
        private LocalDate startDate;
        private LocalDate endDate;
        private Integer budgetAmount;
        private Integer totalExpense;
        private Integer remainingBudget;
        private Integer savedAmount;
        private Integer exceededAmount;
        private Double usageRate;
        private Double savingRate;
        private Integer totalDays;
        private Integer elapsedDays;
        private Integer remainingDays;
        private Double progressRate;
        private Integer dailyRecommendedExpense;
    }

    @Getter
    @Builder
    public static class DashboardAlerts {
        private boolean showWarning;
        private boolean showOverBudget;
        private boolean showPeriodComplete;

        private WarningDetail warning;
        private OverBudgetDetail overBudget;
        private PeriodCompleteDetail periodComplete;
    }

    // 50% 초과 경고 상세 정보
    @Getter
    @Builder
    public static class WarningDetail {
        private String title;
        private String message;
        private Integer dailyRecommendedExpense;
    }

    // 예산 초과 상세 정보
    @Getter
    @Builder
    public static class OverBudgetDetail {
        private String title;
        private String message;
        private Integer exceededAmount;
    }

    // 기간 종료(성공) 상세 정보
    @Getter
    @Builder
    public static class PeriodCompleteDetail {
        private String title;
        private String message1;
        private String message2;
        private Integer savedAmount;
    }
}