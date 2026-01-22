package com.leets.monifit_be.domain.expense.controller;

import com.leets.monifit_be.domain.expense.dto.CalendarResponse;
import com.leets.monifit_be.domain.expense.service.CalendarService;
import com.leets.monifit_be.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(summary = "월별 지출 합계 조회")
    @GetMapping("/monthly")
    public ResponseEntity<ApiResponse<CalendarResponse.MonthlySummary>> getMonthlySummary(
            @AuthenticationPrincipal Long memberId,
            @RequestParam("year") int year,
            @RequestParam("month") int month) {

        CalendarResponse.MonthlySummary response = calendarService.getMonthlySummary(memberId, year, month);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "특정 날짜 지출 상세 조회")
    @GetMapping("/daily")
    public ResponseEntity<ApiResponse<CalendarResponse.DailyDetail>> getDailyDetail(
            @AuthenticationPrincipal Long memberId,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        CalendarResponse.DailyDetail response = calendarService.getDailyDetail(memberId, date);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}