package com.leets.monifit_be.domain.budget.controller;

import com.leets.monifit_be.domain.budget.dto.*;
import com.leets.monifit_be.domain.budget.service.BudgetPeriodService;
import com.leets.monifit_be.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * 예산 기간 API 컨트롤러
 * - POST /budget-periods: 목표 기간/예산 설정 (생성)
 * - GET /budget-periods/active: 활성 예산 기간 조회 (없으면 404)
 * - GET /budget-periods/completed: 완료된 기간 목록 조회 (리포트용)
 * - GET /budget-periods/{periodId}: 특정 기간 상세 조회 (리포트 상세)
 */
@Tag(name = "BudgetPeriod", description = "예산 기간 API")
@RestController
@RequestMapping("/api/v1/budget-periods")
@RequiredArgsConstructor
public class BudgetPeriodController {

    private final BudgetPeriodService budgetPeriodService;

    /**
     * 예산 기간 생성
     * 목표 기간 및 예산을 설정합니다.
     * 시작일은 오늘만 가능하며, 기간은 자동으로 30일로 설정됩니다.
     * 이미 활성 기간이 있으면 생성할 수 없습니다.
     *
     * 인증 필요 (Authorization: Bearer {accessToken})
     */
    @Operation(summary = "예산 기간 생성", description = "목표 기간 및 예산을 설정합니다. 시작일은 오늘, 기간은 30일로 자동 설정됩니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<BudgetPeriodResponse>> create(
            @AuthenticationPrincipal Long memberId,
            @Valid @RequestBody BudgetPeriodCreateRequest request) {

        BudgetPeriodResponse response = budgetPeriodService.create(memberId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    /**
     * 활성 예산 기간 조회
     * 현재 활성화된 예산 기간을 조회합니다.
     * 활성 기간이 없으면 404를 반환합니다.
     * 클라이언트는 404 응답 시 목표 설정 화면으로 이동해야 합니다.
     *
     * 인증 필요 (Authorization: Bearer {accessToken})
     */
    @Operation(summary = "활성 예산 기간 조회", description = "현재 활성화된 예산 기간을 조회합니다. 활성 기간이 없으면 404를 반환합니다.")
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<ActiveBudgetPeriodResponse>> getActivePeriod(
            @AuthenticationPrincipal Long memberId) {

        ActiveBudgetPeriodResponse response = budgetPeriodService.getActivePeriod(memberId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 완료된 기간 목록 조회 (리포트용)
     * 완료된 예산 기간 목록을 종료일 기준 최신순으로 조회합니다.
     * 완료된 기간이 없으면 빈 배열을 반환합니다.
     *
     * 인증 필요 (Authorization: Bearer {accessToken})
     */
    @Operation(summary = "완료된 기간 목록 조회", description = "완료된 예산 기간 목록을 조회합니다. (리포트용)")
    @GetMapping("/completed")
    public ResponseEntity<ApiResponse<CompletedPeriodsResponse>> getCompletedPeriods(
            @AuthenticationPrincipal Long memberId) {

        CompletedPeriodsResponse response = budgetPeriodService.getCompletedPeriods(memberId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 특정 기간 상세 조회 (리포트 상세)
     * 특정 예산 기간의 상세 정보를 조회합니다.
     * 총 지출, 남은 예산, 절약 금액 등 계산된 정보가 포함됩니다.
     * 본인의 기간만 조회할 수 있습니다.
     *
     * 인증 필요 (Authorization: Bearer {accessToken})
     */
    @Operation(summary = "기간 상세 조회", description = "특정 예산 기간의 상세 정보를 조회합니다. (리포트 상세)")
    @GetMapping("/{periodId}")
    public ResponseEntity<ApiResponse<BudgetPeriodDetailResponse>> getPeriodDetail(
            @AuthenticationPrincipal Long memberId,
            @Parameter(name = "periodId", description = "조회할 예산 기간 ID", required = true, in = ParameterIn.PATH) @PathVariable("periodId") Long periodId) {

        BudgetPeriodDetailResponse response = budgetPeriodService.getPeriodDetail(memberId, periodId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
