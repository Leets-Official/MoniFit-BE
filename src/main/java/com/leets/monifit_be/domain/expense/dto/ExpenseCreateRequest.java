package com.leets.monifit_be.domain.expense.dto;

import com.leets.monifit_be.domain.expense.entity.ExpenseCategory;
import java.time.LocalDate;

public class ExpenseCreateRequest {
    private Integer amount;
    private ExpenseCategory category;
    private LocalDate spentDate;

    public ExpenseCreateRequest() {}

    public ExpenseCreateRequest(Integer amount, ExpenseCategory category, LocalDate spentDate) {
        this.amount = amount;
        this.category = category;
        this.spentDate = spentDate;
    }

    public Integer getAmount() { return amount; }
    public ExpenseCategory getCategory() { return category; }
    public LocalDate getSpentDate() { return spentDate; }
}