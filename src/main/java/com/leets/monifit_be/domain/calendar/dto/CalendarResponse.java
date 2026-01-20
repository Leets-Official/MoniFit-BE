package com.leets.monifit_be.domain.calendar.dto;

import java.util.List;

public class CalendarResponse {
    private Long dailyTotal;
    private List<ExpenseDetail> items;

    public CalendarResponse(Long dailyTotal, List<ExpenseDetail> items) {
        this.dailyTotal = dailyTotal;
        this.items = items;
    }

    public Long getDailyTotal() { return dailyTotal; }
    public List<ExpenseDetail> getItems() { return items; }

    // 내부 클래스로 상세 항목 정의
    public static class ExpenseDetail {
        private Long expenseId;
        private String category;
        private Integer amount;

        public ExpenseDetail(Long expenseId, String category, Integer amount) {
            this.expenseId = expenseId;
            this.category = category;
            this.amount = amount;
        }

        public Long getExpenseId() { return expenseId; }
        public String getCategory() { return category; }
        public Integer getAmount() { return amount; }
    }
}