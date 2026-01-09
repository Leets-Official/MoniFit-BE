package com.leets.monifit_be.domain.budget.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PeriodStatus {
    ACTIVE("활성"),
    COMPLETED("완료");

    private final String description;
}
