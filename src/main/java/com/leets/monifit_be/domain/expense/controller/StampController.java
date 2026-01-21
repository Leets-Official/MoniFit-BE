package com.leets.monifit_be.domain.expense.controller;

import com.leets.monifit_be.domain.expense.dto.StampResponse;
import com.leets.monifit_be.domain.expense.service.StampService;
import com.leets.monifit_be.global.response.ApiResponse;
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

    @GetMapping
    public ResponseEntity<ApiResponse<StampResponse>> getMonthlyStamps(
            @AuthenticationPrincipal Long memberId,
            @RequestParam int year,
            @RequestParam int month) {

        StampResponse response = stampService.getMonthlyStamps(memberId, year, month);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}