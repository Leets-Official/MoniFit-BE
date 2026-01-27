package com.leets.monifit_be.domain.expense.service;

import com.leets.monifit_be.domain.budget.entity.BudgetPeriod;
import com.leets.monifit_be.domain.budget.entity.PeriodStatus;
import com.leets.monifit_be.domain.budget.repository.BudgetPeriodRepository;
import com.leets.monifit_be.domain.expense.dto.CalendarResponse;
import com.leets.monifit_be.domain.expense.entity.Expense;
import com.leets.monifit_be.domain.expense.entity.ExpenseCategory;
import com.leets.monifit_be.domain.expense.repository.ExpenseRepository;
import com.leets.monifit_be.global.exception.BusinessException;
import com.leets.monifit_be.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CalendarService {

        private final ExpenseRepository expenseRepository;
        private final BudgetPeriodRepository budgetPeriodRepository;

        @Transactional(readOnly = true)
        public CalendarResponse.MonthlySummary getMonthlySummary(Long memberId, int year, int month) {
                BudgetPeriod period = budgetPeriodRepository.findByMemberIdAndStatus(memberId, PeriodStatus.ACTIVE)
                                .orElseThrow(() -> new BusinessException(ErrorCode.ACTIVE_PERIOD_NOT_FOUND));

                YearMonth yearMonth = YearMonth.of(year, month);
                List<CalendarResponse.DailySummary> dailySummaries = new ArrayList<>();
                List<Expense> monthlyExpenses = expenseRepository.findAllByMemberAndMonth(memberId, year, month);

                for (int day = 1; day <= yearMonth.lengthOfMonth(); day++) {
                        LocalDate date = yearMonth.atDay(day);
                        int totalAmount = monthlyExpenses.stream()
                                        .filter(e -> e.getSpentDate().isEqual(date))
                                        .mapToInt(Expense::getAmount)
                                        .sum();

                        boolean isWithinPeriod = !date.isBefore(period.getStartDate())
                                        && !date.isAfter(period.getEndDate());

                        dailySummaries.add(CalendarResponse.DailySummary.builder()
                                        .date(date)
                                        .totalAmount(totalAmount)
                                        .isWithinPeriod(isWithinPeriod)
                                        .build());
                }

                return CalendarResponse.MonthlySummary.builder()
                                .year(year)
                                .month(month)
                                .period(CalendarResponse.PeriodInfoDto.builder()
                                                .id(period.getId())
                                                .startDate(period.getStartDate())
                                                .endDate(period.getEndDate())
                                                .build())
                                .dailySummaries(dailySummaries)
                                .build();
        }

        @Transactional(readOnly = true)
        public CalendarResponse.DailyDetail getDailyDetail(Long memberId, LocalDate date) {
                BudgetPeriod period = budgetPeriodRepository.findByMemberIdAndStatus(memberId, PeriodStatus.ACTIVE)
                                .orElseThrow(() -> new BusinessException(ErrorCode.ACTIVE_PERIOD_NOT_FOUND));

                List<Expense> dailyExpenses = expenseRepository.findByBudgetPeriodAndSpentDate(period, date);
                int totalAmount = dailyExpenses.stream().mapToInt(Expense::getAmount).sum();

                List<CalendarResponse.CategoryDetail> categoryDetails = Arrays.stream(ExpenseCategory.values())
                                .map(category -> {
                                        List<CalendarResponse.ExpenseItem> items = dailyExpenses.stream()
                                                        .filter(e -> e.getCategory() == category)
                                                        .map(e -> CalendarResponse.ExpenseItem.builder()
                                                                        .id(e.getId())
                                                                        .amount(e.getAmount())
                                                                        .createdAt(e.getCreatedAt().toString())
                                                                        .build())
                                                        .collect(Collectors.toList());

                                        return CalendarResponse.CategoryDetail.builder()
                                                        .category(category.name())
                                                        .categoryName(category.getDisplayName())
                                                        .totalAmount(items.stream().mapToInt(
                                                                        CalendarResponse.ExpenseItem::getAmount).sum())
                                                        .expenses(items)
                                                        .build();
                                })
                                .collect(Collectors.toList());

                return CalendarResponse.DailyDetail.builder()
                                .date(date)
                                .totalAmount(totalAmount)
                                .categories(categoryDetails)
                                .build();
        }
}