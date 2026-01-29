package com.leets.monifit_be.domain.budget.dto;

import com.leets.monifit_be.domain.budget.entity.BudgetPeriod;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

/**
 * 예산 기간 상세 응답 DTO (리포트 상세용)
 * GET /budget-periods/{periodId} 응답에 사용
 *
 * 리포트 상세에서 필요한 정보:
 * - 기본 예산 기간 정보
 * - 총 지출 금액
 * - 절약/초과 금액
 * - 카테고리별 지출 내역 (도넛 차트용)
 */
@Schema(description = "예산 기간 상세 응답 (리포트 상세)")
@Getter
@Builder
public class BudgetPeriodDetailResponse {

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

    @Schema(description = "상태 (ACTIVE / COMPLETED)", example = "COMPLETED")
    private String status;

    @Schema(description = "완료 유형 (SUCCESS / OVER_BUDGET)", example = "SUCCESS")
    private String completionType;

    @Schema(description = "절약 금액 (원, 초과 시 null)", example = "38000")
    private Integer savedAmount;

    @Schema(description = "초과 금액 (원, 절약 시 null)", example = "null")
    private Integer exceededAmount;

    @Schema(description = "카테고리별 지출 내역")
    private List<CategoryExpense> categoryExpenses;

    /**
     * Entity -> DTO 변환
     *
     * @param budgetPeriod     예산 기간 엔티티
     * @param totalExpense     총 지출 금액
     * @param categoryExpenses 카테고리별 지출 내역
     */
    public static BudgetPeriodDetailResponse from(
            BudgetPeriod budgetPeriod,
            int totalExpense,
            List<CategoryExpense> categoryExpenses) {

        int budget = budgetPeriod.getBudgetAmount();
        int difference = budget - totalExpense;

        Integer saved = difference >= 0 ? difference : null;
        Integer exceeded = difference < 0 ? Math.abs(difference) : null;

        return BudgetPeriodDetailResponse.builder()
                .id(budgetPeriod.getId())
                .startDate(budgetPeriod.getStartDate())
                .endDate(budgetPeriod.getEndDate())
                .budgetAmount(budget)
                .totalExpense(totalExpense)
                .status(budgetPeriod.getStatus().name())
                .completionType(budgetPeriod.getCompletionType() != null
                        ? budgetPeriod.getCompletionType().name()
                        : null)
                .savedAmount(saved)
                .exceededAmount(exceeded)
                .categoryExpenses(categoryExpenses)
                .build();
    }
}
