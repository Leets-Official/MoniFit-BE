package com.leets.monifit_be.domain.expense.service;

import com.leets.monifit_be.domain.budget.entity.BudgetPeriod;
import com.leets.monifit_be.domain.budget.entity.PeriodStatus;
import com.leets.monifit_be.domain.budget.repository.BudgetPeriodRepository;
import com.leets.monifit_be.domain.expense.dto.StampResponse;
import com.leets.monifit_be.domain.expense.repository.ExpenseRepository;
import com.leets.monifit_be.global.exception.ActiveBudgetNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StampService {

    private final ExpenseRepository expenseRepository;
    private final BudgetPeriodRepository budgetPeriodRepository; // 예산 기간 조회를 위해 추가

    @Transactional(readOnly = true)
    public StampResponse getMonthlyStamps(Long memberId, int year, int month) {

        // 현재 활성화된 예산 기간 조회
        BudgetPeriod period = budgetPeriodRepository.findByMemberIdAndStatus(memberId, PeriodStatus.ACTIVE)
                .orElseThrow(() -> new ActiveBudgetNotFoundException("활성화된 예산 기간이 없습니다."));

        // 해당 월의 지출 날짜 리스트 조회
        List<LocalDate> dates = expenseRepository.findDatesByMonth(memberId, year, month);

        // 추가된 필드를 포함하여 StampResponse 생성
        LocalDate today = LocalDate.now();

        return new StampResponse(
                dates,
                period.getStartDate(),              // 예산 시작일
                period.getEndDate(),                // 예산 종료일
                today,                              // 오늘 날짜
                today.isEqual(period.getEndDate())  // 오늘이 종료일(마지막 날)인지 여부
        );
    }
}