package com.leets.monifit_be.domain.expense.dto;

import com.leets.monifit_be.domain.expense.entity.ExpenseCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 지출 입력 요청 DTO
 * POST /expenses 요청에 사용
 */
@Schema(description = "지출 입력 요청")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseCreateRequest {

    @Schema(description = "카테고리 (FOOD, SHOPPING, MEDICAL, LIVING, ETC)", example = "FOOD")
    @NotNull(message = "카테고리를 선택해주세요")
    private ExpenseCategory category;

    @Schema(description = "지출 금액 (원, 양수)", example = "15000")
    @NotNull(message = "금액을 입력해주세요")
    @Positive(message = "금액은 양수여야 합니다")
    private Integer amount;

    @Schema(description = "지출 날짜 (기본값: 오늘)", example = "2026-01-28")
    private LocalDate spentDate;
}