package com.leets.monifit_be.domain.expense.dto;

import lombok.*;
import java.time.LocalDate;
import java.util.List;

public class CalendarResponse {

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MonthlySummary {
        private Integer year;
        private Integer month;
        private PeriodInfoDto period;
        private List<DailySummary> dailySummaries;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DailyDetail {
        private LocalDate date;
        private Integer totalAmount;
        private List<CategoryDetail> categories;
    }

    @Getter
    @Builder
    public static class PeriodInfoDto {
        private Long id;
        private LocalDate startDate;
        private LocalDate endDate;
    }

    @Getter
    @Builder
    public static class DailySummary {
        private LocalDate date;
        private Integer totalAmount;
        private boolean isWithinPeriod; // 활성 기간 내 날짜 여부
    }

    @Getter
    @Builder
    public static class CategoryDetail {
        private String category;      // 카테고리 코드 (예: FOOD)
        private String categoryName;  // 카테고리 한글명 (예: 식비)
        private Integer totalAmount;  // 카테고리별 합계
        private List<ExpenseItem> expenses; // 해당 카테고리의 개별 지출 리스트
    }

    @Getter
    @Builder
    public static class ExpenseItem {
        private Long id;
        private Integer amount;
        private String createdAt;
    }
}