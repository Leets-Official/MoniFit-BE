package com.leets.monifit_be.domain.expense.controller;

import com.leets.monifit_be.domain.expense.dto.ExpenseCreateRequest;
import com.leets.monifit_be.domain.expense.dto.ExpenseCreateResponse;
import com.leets.monifit_be.domain.expense.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    @PostMapping
    public ResponseEntity<ExpenseCreateResponse> createExpense(
            @AuthenticationPrincipal Long memberId,
            @RequestBody ExpenseCreateRequest request) {
        ExpenseCreateResponse response = expenseService.createExpense(memberId, request);
        return ResponseEntity.ok(response);
    }
}