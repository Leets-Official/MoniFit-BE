package com.leets.monifit_be.domain.expense.controller;

import com.leets.monifit_be.domain.expense.dto.ExpenseCreateRequest;
import com.leets.monifit_be.domain.expense.dto.ExpenseCreateResponse;
import com.leets.monifit_be.domain.expense.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    @PostMapping
    public ResponseEntity<ExpenseCreateResponse> createExpense(@RequestBody ExpenseCreateRequest request) {
        Long tempMemberId = 1L;
        ExpenseCreateResponse response = expenseService.createExpense(tempMemberId,request);
        return ResponseEntity.ok(response);
    }
}