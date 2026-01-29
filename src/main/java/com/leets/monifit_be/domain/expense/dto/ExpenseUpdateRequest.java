package com.leets.monifit_be.domain.expense.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 지출 수정 요청 DTO
 * PATCH /expenses/{expenseId} 요청에 사용
 */
@Schema(description = "지출 수정 요청")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseUpdateRequest {

    @Schema(description = "새 금액 (원, 양수)", example = "20000")
    @NotNull(message = "금액을 입력해주세요")
    @Positive(message = "금액은 양수여야 합니다")
    private Integer amount;
}
