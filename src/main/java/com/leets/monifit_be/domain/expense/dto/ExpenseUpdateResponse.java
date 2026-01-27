package com.leets.monifit_be.domain.expense.dto;

import lombok.*;
import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseUpdateResponse {
    private ExpenseDto expense;
    private boolean periodCompleted;
    private String completionType;
    private Integer exceededAmount;
    private AlertsDto alerts;
    private UpdatedBudgetDto updatedBudget;

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ExpenseDto {
        private Long id;
        private String category;
        private String categoryName;
        private Integer amount;
        private LocalDate spentDate;
        private String createdAt;
        private String updatedAt;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AlertsDto {
        private boolean showWarning;
        private AlertDetail warning;
        private boolean showOverBudget;
        private AlertDetail overBudget;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AlertDetail {
        private String title;
        private String message;
        private Integer dailyRecommendedExpense;
        private Integer exceededAmount;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UpdatedBudgetDto {
        private Integer totalExpense;
        private Integer remainingBudget;
        private Double usageRate;
    }
}
