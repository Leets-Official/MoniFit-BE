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
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StampService {

    private final ExpenseRepository expenseRepository;
    private final BudgetPeriodRepository budgetPeriodRepository;

    @Transactional(readOnly = true)
    public StampResponse getActivePeriodStamps(Long memberId) {
        // 1. 활성 예산 기간 조회 (명세서 7-1)
        BudgetPeriod period = budgetPeriodRepository.findByMemberIdAndStatus(memberId, PeriodStatus.ACTIVE)
                .orElseThrow(() -> new ActiveBudgetNotFoundException("활성화된 예산 기간이 없습니다."));

        LocalDate today = LocalDate.now();
        List<StampResponse.StampDetail> stampDetails = new ArrayList<>();
        int stampedDaysCount = 0;

        // 2. 시작일부터 종료일까지 30일간 반복하며 데이터 생성 (명세서 7-1)
        for (int i = 0; i < 30; i++) {
            LocalDate currentDate = period.getStartDate().plusDays(i);

            // 3. 스탬프 활성 조건 체크 (명세서 6번)
            // 조건: 해당 날짜에 지출이 있고, 그 지출이 당일에 입력되었는가 (DATE(created_at) = spent_date)
            boolean isStamped = expenseRepository.existsValidStamp(period.getId(), currentDate);

            if (isStamped) stampedDaysCount++;

            // 4. 날짜별 상세 정보 조립
            stampDetails.add(StampResponse.StampDetail.builder()
                    .date(currentDate)
                    .dayNumber(i + 1)
                    .isStamped(isStamped)
                    .isToday(currentDate.isEqual(today))
                    .isLastDay(currentDate.isEqual(period.getEndDate()))
                    .build());
        }

        // 5. 최종 응답 DTO 생성 (Builder 활용)
        return StampResponse.builder()
                .period(StampResponse.PeriodInfoDto.builder()
                        .id(period.getId())
                        .startDate(period.getStartDate())
                        .endDate(period.getEndDate())
                        .build())
                .today(today)
                .stamps(stampDetails)
                .totalDays(30)
                .stampedDays(stampedDaysCount)
                .build();
    }
}