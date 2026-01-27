package com.leets.monifit_be.domain.expense.dto;

import lombok.*;
import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseCreateResponse {

    private ExpenseDto expense;           // 생성된 지출 상세 정보
    private boolean periodCompleted;      // 예산 초과 등으로 인한 기간 종료 여부
    private String completionType;        // 종료 유형 (OVER_BUDGET 등)
    private Integer exceededAmount;       // 초과된 금액 (초과 시에만)
    private AlertsDto alerts;             // 명세서 핵심인 중첩 알림 객체
    private UpdatedBudgetDto updatedBudget; // 업데이트된 예산 현황

    @Getter
    @Builder
    public static class ExpenseDto {
        private Long id;
        private String category;
        private String categoryName;
        private Integer amount;
        private LocalDate spentDate;
        private String createdAt;
    }

    @Getter
    @Builder
    public static class AlertsDto {
        private AlertDetail expenseInput; // 지출 입력 팝업 (항상 표시)
        private boolean showStamp;        // 스탬프 팝업 표시 여부
        private AlertDetail stamp;        // 스탬프 상세 메시지
        private boolean showWarning;      // 50% 경고 표시 여부
        private AlertDetail warning;      // 50% 경고 상세 메시지
        private boolean showOverBudget;   // 예산 초과 알림 표시 여부
        private AlertDetail overBudget;   // 예산 초과 상세 메시지
    }

    @Getter
    @Builder
    public static class AlertDetail {
        private String title;
        private String message;
    }

    @Getter
    @Builder
    public static class UpdatedBudgetDto {
        private Integer totalExpense;
        private Integer remainingBudget;
        private Double usageRate;
    }
}