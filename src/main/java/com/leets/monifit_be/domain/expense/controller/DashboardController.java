package com.leets.monifit_be.domain.expense.controller;

import com.leets.monifit_be.domain.expense.dto.DashboardResponse;
import com.leets.monifit_be.domain.expense.service.DashboardService;
import com.leets.monifit_be.global.response.ApiResponse; // 패키지 경로 확인
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Dashboard", description = "대시보드 API")
@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<ApiResponse<DashboardResponse>> getDashboard(
            @AuthenticationPrincipal Long memberId) {
        DashboardResponse response = dashboardService.getDashboardData(memberId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}