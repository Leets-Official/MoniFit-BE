package com.leets.monifit_be.domain.expense.controller;

import com.leets.monifit_be.domain.expense.dto.ExpenseCreateRequest;
import com.leets.monifit_be.domain.expense.dto.ExpenseCreateResponse;
import com.leets.monifit_be.domain.expense.dto.ExpenseListResponse;
import com.leets.monifit_be.domain.expense.dto.ExpenseUpdateRequest;
import com.leets.monifit_be.domain.expense.dto.ExpenseUpdateResponse;
import com.leets.monifit_be.domain.expense.dto.ExpenseDeleteResponse;
import com.leets.monifit_be.domain.expense.service.ExpenseService;
import com.leets.monifit_be.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Tag(name = "Expense", description = "지출 API")
@RestController
@RequestMapping("/api/v1/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    @Operation(summary = "지출 입력", description = "새로운 지출을 입력합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<ExpenseCreateResponse>> createExpense(
            @AuthenticationPrincipal Long memberId,
            @RequestBody ExpenseCreateRequest request) {
        ExpenseCreateResponse response = expenseService.createExpense(memberId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @Operation(summary = "지출 목록 조회", description = "지출 내역을 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<ExpenseListResponse>> getExpenses(
            @AuthenticationPrincipal Long memberId,
            @Parameter(description = "특정 기간 ID (기본값: 활성 기간)") @RequestParam(required = false) Long periodId,
            @Parameter(description = "특정 날짜 필터") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @Parameter(description = "카테고리 필터") @RequestParam(required = false) String category) {
        ExpenseListResponse response = expenseService.getExpenses(memberId, periodId, date, category);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "지출 수정", description = "기존 지출 내역을 수정합니다.")
    @PatchMapping("/{expenseId}")
    public ResponseEntity<ApiResponse<ExpenseUpdateResponse>> updateExpense(
            @AuthenticationPrincipal Long memberId,
            @Parameter(description = "지출 ID") @PathVariable Long expenseId,
            @RequestBody ExpenseUpdateRequest request) {
        ExpenseUpdateResponse response = expenseService.updateExpense(memberId, expenseId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "지출 삭제", description = "기존 지출 내역을 삭제합니다.")
    @DeleteMapping("/{expenseId}")
    public ResponseEntity<ApiResponse<ExpenseDeleteResponse>> deleteExpense(
            @AuthenticationPrincipal Long memberId,
            @Parameter(description = "지출 ID") @PathVariable Long expenseId) {
        ExpenseDeleteResponse response = expenseService.deleteExpense(memberId, expenseId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}