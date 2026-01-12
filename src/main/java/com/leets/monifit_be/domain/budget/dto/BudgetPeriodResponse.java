package com.leets.monifit_be.domain.budget.dto;

import com.leets.monifit_be.domain.budget.entity.BudgetPeriod;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 예산 기간 응답 DTO
 * - POST /budget-periods 응답
 * - GET /budget-periods/active 응답
 * - GET /budget-periods/completed 목록 응답
 */
@Getter
@Builder
public class BudgetPeriodResponse {

    private Long id;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer budgetAmount;
    private String status;
    private String completionType;
    private Boolean warningShown;
    private LocalDateTime createdAt;

    /**
     * Entity -> DTO 변환
     */
    public static BudgetPeriodResponse from(BudgetPeriod budgetPeriod) {
        return BudgetPeriodResponse.builder()
                .id(budgetPeriod.getId())
                .startDate(budgetPeriod.getStartDate())
                .endDate(budgetPeriod.getEndDate())
                .budgetAmount(budgetPeriod.getBudgetAmount())
                .status(budgetPeriod.getStatus().name())
                .completionType(budgetPeriod.getCompletionType() != null
                        ? budgetPeriod.getCompletionType().name()
                        : null)
                .warningShown(budgetPeriod.getWarningShown())
                .createdAt(budgetPeriod.getCreatedAt())
                .build();
    }
}
