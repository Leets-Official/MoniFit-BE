package com.leets.monifit_be.domain.budget.dto;

import com.leets.monifit_be.domain.budget.entity.BudgetPeriod;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

/**
 * 완료된 기간 목록 아이템 DTO
 * CompletedPeriodsResponse 내부에서 사용
 */
@Schema(description = "완료된 기간 아이템")
@Getter
@Builder
public class CompletedPeriodItem {

    @Schema(description = "예산 기간 ID", example = "9")
    private Long id;

    @Schema(description = "시작일", example = "2025-12-23")
    private LocalDate startDate;

    @Schema(description = "종료일", example = "2026-01-21")
    private LocalDate endDate;

    @Schema(description = "목표 예산 (원)", example = "400000")
    private Integer budgetAmount;

    @Schema(description = "총 지출 금액 (원)", example = "362000")
    private Integer totalExpense;

    @Schema(description = "절약 금액 (원, SUCCESS인 경우)", example = "38000")
    private Integer savedAmount;

    @Schema(description = "초과 금액 (원, OVER_BUDGET인 경우)", example = "null")
    private Integer exceededAmount;

    @Schema(description = "완료 유형 (SUCCESS / OVER_BUDGET)", example = "SUCCESS")
    private String completionType;

    /**
     * Entity -> DTO 변환
     *
     * @param budgetPeriod 예산 기간 엔티티
     * @param totalExpense 총 지출 금액
     */
    public static CompletedPeriodItem from(BudgetPeriod budgetPeriod, long totalExpense) {
        int budget = budgetPeriod.getBudgetAmount();
        int expense = (int) totalExpense;
        int difference = budget - expense;

        Integer saved = difference >= 0 ? difference : null;
        Integer exceeded = difference < 0 ? Math.abs(difference) : null;

        return CompletedPeriodItem.builder()
                .id(budgetPeriod.getId())
                .startDate(budgetPeriod.getStartDate())
                .endDate(budgetPeriod.getEndDate())
                .budgetAmount(budget)
                .totalExpense(expense)
                .savedAmount(saved)
                .exceededAmount(exceeded)
                .completionType(budgetPeriod.getCompletionType() != null
                        ? budgetPeriod.getCompletionType().name()
                        : null)
                .build();
    }
}
