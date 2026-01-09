package com.leets.monifit_be.domain.budget.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CompletionType {
    SUCCESS("정상 종료"),
    OVER_BUDGET("예산 초과 종료");

    private final String description;
}
