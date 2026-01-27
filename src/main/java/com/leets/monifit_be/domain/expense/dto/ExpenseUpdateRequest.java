package com.leets.monifit_be.domain.expense.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseUpdateRequest {
    private Integer amount;
}
