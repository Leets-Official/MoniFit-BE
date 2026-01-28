package com.leets.monifit_be.domain.budget.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDate;

/**
 * 예산 기간 생성 요청 DTO
 * POST /budget-periods 요청에 사용
 *
 * 요구사항:
 * - 시작일: 오늘 포함 이후 날짜만 선택 가능
 * - 기간: 시작일부터 자동으로 30일 (시작일 + 29일)
 * - 예산 금액: 필수 입력
 */
@Getter
public class BudgetPeriodCreateRequest {

    @NotNull(message = "시작일은 필수입니다")
    private LocalDate startDate;

    @NotNull(message = "예산 금액은 필수입니다")
    @Min(value = 1, message = "예산 금액은 양수여야 합니다")
    private Integer budgetAmount;
}
