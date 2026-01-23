package com.leets.monifit_be.domain.budget.dto;

import com.leets.monifit_be.domain.budget.entity.BudgetPeriod;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 예산 기간 상세 응답 DTO (리포트 상세용)
 * GET /budget-periods/{periodId} 응답에 사용
 *
 * 리포트 상세에서 필요한 정보:
 * - 기본 예산 기간 정보
 * - 총 지출 금액 (계산)
 * - 남은 예산 (계산)
 * - 절약/초과 금액 (계산)
 */
@Getter
@Builder
public class BudgetPeriodDetailResponse {

    private Long id;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer budgetAmount;
    private String status;
    private String completionType;
    private Boolean warningShown;
    private LocalDateTime createdAt;

    // 계산 필드 (Expense 구현 후 실제 값으로 대체 예정)
    private Integer totalExpense; // 총 지출 금액
    private Integer remainingBudget; // 남은 예산 (budgetAmount - totalExpense)
    private Integer savedAmount; // 절약 금액 (양수면 절약, 음수면 초과)

    /**
     * Entity -> DTO 변환
     * 현재는 지출 정보 없이 기본 정보만 반환
     * totalExpense는 0, remainingBudget은 budgetAmount로 설정
     *
     * @param budgetPeriod 예산 기간 엔티티
     * @param totalExpense 총 지출 금액 (Expense 집계 결과, 없으면 0)
     */
    public static BudgetPeriodDetailResponse from(BudgetPeriod budgetPeriod, Integer totalExpense) {
        int expense = totalExpense != null ? totalExpense : 0;
        int remaining = budgetPeriod.getBudgetAmount() - expense;
        int saved = remaining; // 남은 금액이 곧 절약 금액 (초과 시 음수)

        return BudgetPeriodDetailResponse.builder()
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
                .totalExpense(expense)
                .remainingBudget(remaining)
                .savedAmount(saved)
                .build();
    }
}
