package com.leets.monifit_be.domain.budget.controller;

import com.leets.monifit_be.domain.budget.dto.response.DashboardResponse;
import com.leets.monifit_be.domain.budget.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    public ResponseEntity<DashboardResponse> getDashboard() {
        // 명세서: 메인 화면 데이터 조회 (인증 O)
        Long memberId = 1L;
        return ResponseEntity.ok(dashboardService.getDashboardData(memberId));
    }
}