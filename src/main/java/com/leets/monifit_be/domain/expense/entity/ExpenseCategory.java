package com.leets.monifit_be.domain.expense.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExpenseCategory {
    FOOD("식비"),
    SHOPPING("쇼핑"),
    MEDICAL("의료"),
    LIVING("생활"),
    ETC("기타");

    private final String displayName;
}
