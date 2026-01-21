package com.leets.monifit_be.domain.expense.controller;

import com.leets.monifit_be.domain.expense.dto.CalendarResponse;
import com.leets.monifit_be.domain.expense.service.CalendarService;
import com.leets.monifit_be.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@Tag(name = "Calendar", description = "캘린더 API")
@RestController
@RequestMapping("/api/v1/calendar")
@RequiredArgsConstructor
public class CalendarController {

    private final CalendarService calendarService;

    @GetMapping("/monthly")
    public ResponseEntity<ApiResponse<Object>> getMonthlySummary(
            @AuthenticationPrincipal Long memberId,
            @RequestParam("yearMonth") String yearMonth) { // 예: "2024-03"
        // 추후 서비스 로직 연결 필요
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/daily") // 경로 추가
    public ResponseEntity<ApiResponse<CalendarResponse>> getDailyDetail(
            @AuthenticationPrincipal Long memberId,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        CalendarResponse response = calendarService.getDailyExpenses(memberId, date);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}