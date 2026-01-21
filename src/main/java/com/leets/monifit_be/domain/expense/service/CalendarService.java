package com.leets.monifit_be.domain.expense.service;

import com.leets.monifit_be.domain.budget.entity.BudgetPeriod;
import com.leets.monifit_be.domain.budget.entity.PeriodStatus; // 1. 상태 Enum 임포트
import com.leets.monifit_be.domain.budget.repository.BudgetPeriodRepository;
import com.leets.monifit_be.domain.expense.dto.CalendarResponse; // 2. DTO 패키지 경로 확인
import com.leets.monifit_be.domain.expense.entity.Expense;
import com.leets.monifit_be.domain.expense.repository.ExpenseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CalendarService {

    private final ExpenseRepository expenseRepository;
    private final BudgetPeriodRepository budgetPeriodRepository;

    public CalendarService(ExpenseRepository expenseRepository, BudgetPeriodRepository budgetPeriodRepository) {
        this.expenseRepository = expenseRepository;
        this.budgetPeriodRepository = budgetPeriodRepository;
    }

    @Transactional(readOnly = true)
    public CalendarResponse getDailyExpenses(Long memberId, LocalDate date) {
        // 1. 현재 활성화된 기간 정보 조회 (요구사항 5번: 활성 기간 데이터만 표시)
        // Repository의 메서드명과 상태(ACTIVE)를 정확히 매칭시킵니다.
        BudgetPeriod activePeriod = budgetPeriodRepository.findByMemberIdAndStatus(memberId, PeriodStatus.ACTIVE)
                .orElseThrow(() -> new IllegalArgumentException("활성화된 예산 기간이 없습니다."));

        // 2. 활성화 기간 범위 밖의 날짜 요청 시 빈 결과 반환 (요구사항 5번 규칙)
        if (date.isBefore(activePeriod.getStartDate()) || date.isAfter(activePeriod.getEndDate())) {
            return new CalendarResponse(0L, List.of());
        }

        // 3. 해당 날짜의 지출 조회 (사용자 ID 필터링 포함)
        List<Expense> expenses = expenseRepository.findBySpentDateAndBudgetPeriodMemberId(date, memberId);

        // 4. 일일 총액 계산
        long total = expenses.stream()
                .mapToLong(Expense::getAmount)
                .sum();

        // 5. 상세 항목 DTO 변환 (카테고리 한글명 포함)
        List<CalendarResponse.ExpenseDetail> details = expenses.stream()
                .map(e -> new CalendarResponse.ExpenseDetail(
                        e.getId(),
                        e.getCategory().getDisplayName(), // Enum의 한글명 사용
                        e.getAmount()
                ))
                .collect(Collectors.toList());

        return new CalendarResponse(total, details);
    }
}