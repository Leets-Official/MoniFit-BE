package com.leets.monifit_be.domain.expense.dto;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseDeleteResponse {
    private UpdatedBudgetDto updatedBudget;

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
