package com.leets.monifit_be.domain.expense.dto;

public class ExpenseCreateResponse {
    private Long expenseId;
    private boolean periodCompleted;
    private String completionType;
    private String message;

    public ExpenseCreateResponse(Long expenseId, boolean periodCompleted, String completionType, String message) {
        this.expenseId = expenseId;
        this.periodCompleted = periodCompleted;
        this.completionType = completionType;
        this.message = message;
    }

    // Getters
    public Long getExpenseId() { return expenseId; }
    public boolean isPeriodCompleted() { return periodCompleted; }
    public String getCompletionType() { return completionType; }
    public String getMessage() { return message; }
}