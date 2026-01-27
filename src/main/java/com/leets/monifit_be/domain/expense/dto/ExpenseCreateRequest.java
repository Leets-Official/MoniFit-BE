package com.leets.monifit_be.domain.expense.dto;

import com.leets.monifit_be.domain.expense.entity.ExpenseCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseCreateRequest {
    private ExpenseCategory category;
    private Integer amount;
    private LocalDate spentDate;
}