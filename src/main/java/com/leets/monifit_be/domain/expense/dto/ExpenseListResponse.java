package com.leets.monifit_be.domain.expense.dto;

import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseListResponse {
    private List<ExpenseItem> expenses;
    private Integer totalCount;
    private Integer totalAmount;

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ExpenseItem {
        private Long id;
        private String category;
        private String categoryName;
        private Integer amount;
        private LocalDate spentDate;
        private String createdAt;
    }
}
