package com.leets.monifit_be.domain.expense.controller;

import com.leets.monifit_be.domain.expense.dto.StampResponse;
import com.leets.monifit_be.domain.expense.service.StampService;
import com.leets.monifit_be.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Stamp", description = "스탬프 API")
@RestController
@RequestMapping("/api/v1/stamps")
@RequiredArgsConstructor
public class StampController {

    private final StampService stampService;

    @Operation(summary = "스탬프 현황 조회", description = "특정 예산 기간의 스탬프 현황을 조회합니다. 이전/이후 기간 탐색 기능을 지원합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<StampResponse>> getStamps(
            @AuthenticationPrincipal Long memberId,
            @Parameter(description = "특정 기간 ID (기본값: 현재 활성 기간)") @RequestParam(required = false) Long periodId) {

        StampResponse response = stampService.getStamps(memberId, periodId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}