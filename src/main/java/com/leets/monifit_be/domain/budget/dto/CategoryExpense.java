package com.leets.monifit_be.domain.budget.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

/**
 * 카테고리별 지출 내역 DTO
 * BudgetPeriodDetailResponse에서 사용
 */
@Schema(description = "카테고리별 지출 내역")
@Getter
@Builder
public class CategoryExpense {

    @Schema(description = "카테고리 코드", example = "FOOD")
    private String category;

    @Schema(description = "카테고리 한글명", example = "식비")
    private String categoryName;

    @Schema(description = "지출 금액 (원)", example = "150000")
    private Integer amount;

    @Schema(description = "비율 (%)", example = "41.4")
    private Double percentage;

    public static CategoryExpense of(String category, String categoryName, int amount, double percentage) {
        return CategoryExpense.builder()
                .category(category)
                .categoryName(categoryName)
                .amount(amount)
                .percentage(Math.round(percentage * 10) / 10.0)
                .build();
    }
}
