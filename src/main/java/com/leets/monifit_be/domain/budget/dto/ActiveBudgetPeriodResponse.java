package com.leets.monifit_be.domain.budget.dto;

import com.leets.monifit_be.domain.budget.entity.BudgetPeriod;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * 활성 예산 기간 응답 DTO
 * GET /budget-periods/active 응답에 사용
 *
 * 메인 화면 및 마이페이지에서 필요한 계산된 정보 포함
 */
@Schema(description = "활성 예산 기간 응답")
@Getter
@Builder
public class ActiveBudgetPeriodResponse {

    @Schema(description = "예산 기간 ID", example = "10")
    private Long id;

    @Schema(description = "시작일", example = "2026-01-22")
    private LocalDate startDate;

    @Schema(description = "종료일", example = "2026-02-20")
    private LocalDate endDate;

    @Schema(description = "목표 예산 (원)", example = "300000")
    private Integer budgetAmount;

    @Schema(description = "총 지출 금액 (원)", example = "120000")
    private Integer totalExpense;

    @Schema(description = "남은 예산 (원)", example = "180000")
    private Integer remainingBudget;

    @Schema(description = "절약 금액 (원, 초과 시 null)", example = "180000")
    private Integer savedAmount;

    @Schema(description = "초과 금액 (원, 절약 시 null)", example = "null")
    private Integer exceededAmount;

    @Schema(description = "사용률 (%)", example = "40.0")
    private Double usageRate;

    @Schema(description = "절약률 (%, 100 - usageRate)", example = "60.0")
    private Double savingRate;

    @Schema(description = "총 일수 (30일)", example = "30")
    private Integer totalDays;

    @Schema(description = "경과 일수", example = "10")
    private Integer elapsedDays;

    @Schema(description = "남은 일수", example = "20")
    private Integer remainingDays;

    @Schema(description = "기간 진행률 (%)", example = "33.3")
    private Double progressRate;

    @Schema(description = "일일 권장 지출 (원)", example = "9000")
    private Integer dailyRecommendedExpense;

    @Schema(description = "상태 (ACTIVE)", example = "ACTIVE")
    private String status;

    @Schema(description = "완료 유형 (활성 시 null)", example = "null")
    private String completionType;

    @Schema(description = "50% 경고 표시 여부", example = "false")
    private Boolean warningShown;

    /**
     * Entity -> DTO 변환 (계산된 정보 포함)
     *
     * @param budgetPeriod 예산 기간 엔티티
     * @param totalExpense 총 지출 금액
     */
    public static ActiveBudgetPeriodResponse from(BudgetPeriod budgetPeriod, long totalExpense) {
        int budget = budgetPeriod.getBudgetAmount();
        int expense = (int) totalExpense;
        int remaining = budget - expense;

        // 절약/초과 계산
        Integer saved = remaining >= 0 ? remaining : null;
        Integer exceeded = remaining < 0 ? Math.abs(remaining) : null;

        // 사용률/절약률 계산
        double usageRate = budget > 0 ? (double) expense / budget * 100 : 0;
        double savingRate = 100 - usageRate;

        // 일수 계산
        LocalDate today = LocalDate.now();
        LocalDate startDate = budgetPeriod.getStartDate();
        LocalDate endDate = budgetPeriod.getEndDate();

        int totalDays = (int) ChronoUnit.DAYS.between(startDate, endDate) + 1; // 30일
        int elapsedDays = (int) ChronoUnit.DAYS.between(startDate, today) + 1;
        if (elapsedDays < 1)
            elapsedDays = 1;
        if (elapsedDays > totalDays)
            elapsedDays = totalDays;
        int remainingDays = totalDays - elapsedDays;

        // 진행률 계산
        double progressRate = (double) elapsedDays / totalDays * 100;

        // 일일 권장 지출 계산 (남은 예산 / 남은 일수)
        int dailyRecommended = remainingDays > 0 && remaining > 0 ? remaining / remainingDays : 0;

        return ActiveBudgetPeriodResponse.builder()
                .id(budgetPeriod.getId())
                .startDate(startDate)
                .endDate(endDate)
                .budgetAmount(budget)
                .totalExpense(expense)
                .remainingBudget(remaining)
                .savedAmount(saved)
                .exceededAmount(exceeded)
                .usageRate(Math.round(usageRate * 10) / 10.0)
                .savingRate(Math.round(savingRate * 10) / 10.0)
                .totalDays(totalDays)
                .elapsedDays(elapsedDays)
                .remainingDays(remainingDays)
                .progressRate(Math.round(progressRate * 10) / 10.0)
                .dailyRecommendedExpense(dailyRecommended)
                .status(budgetPeriod.getStatus().name())
                .completionType(budgetPeriod.getCompletionType() != null
                        ? budgetPeriod.getCompletionType().name()
                        : null)
                .warningShown(budgetPeriod.getWarningShown())
                .build();
    }
}
