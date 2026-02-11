package com.leets.monifit_be.domain.expense.service;

import com.leets.monifit_be.domain.budget.entity.BudgetPeriod;
import com.leets.monifit_be.domain.budget.entity.PeriodStatus;
import com.leets.monifit_be.domain.budget.repository.BudgetPeriodRepository;
import com.leets.monifit_be.domain.expense.dto.StampResponse;
import com.leets.monifit_be.domain.expense.repository.ExpenseRepository;
import com.leets.monifit_be.global.exception.BusinessException;
import com.leets.monifit_be.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StampService {

        private final ExpenseRepository expenseRepository;
        private final BudgetPeriodRepository budgetPeriodRepository;

        @Transactional(readOnly = true)
        public StampResponse getStamps(Long memberId, Long periodId) {
                BudgetPeriod period;

                // periodId가 null이면 활성 기간 조회, 아니면 특정 기간 조회
                if (periodId == null) {
                        period = budgetPeriodRepository.findByMemberIdAndStatus(memberId, PeriodStatus.ACTIVE)
                                        .orElseThrow(() -> new BusinessException(ErrorCode.ACTIVE_PERIOD_NOT_FOUND));
                } else {
                        period = budgetPeriodRepository.findById(periodId)
                                        .orElseThrow(() -> new BusinessException(ErrorCode.BUDGET_PERIOD_NOT_FOUND));

                        // 접근 권한 확인
                        if (!period.getMember().getId().equals(memberId)) {
                                throw new BusinessException(ErrorCode.FORBIDDEN);
                        }
                }

                // 이전/다음 기간 탐색 정보 조회
                StampResponse.NavigationDto navigation = buildNavigation(memberId, period);

                LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul")); // 한국 시간대 기준 오늘 날짜
                List<StampResponse.StampDetail> stampDetails = new ArrayList<>();
                int stampedDaysCount = 0;

                // 시작일부터 종료일까지 30일간 반복하며 데이터 생성 (명세서 7-1)
                for (int i = 0; i < 30; i++) {
                        LocalDate currentDate = period.getStartDate().plusDays(i);

                        // 스탬프 활성 조건 체크 (명세서 6번)
                        // 조건: 해당 날짜에 지출이 있고, 그 지출이 당일에 입력되었는가 (DATE(created_at) = spent_date)
                        boolean isStamped = expenseRepository.existsValidStamp(period.getId(), currentDate);

                        if (isStamped)
                                stampedDaysCount++;

                        stampDetails.add(StampResponse.StampDetail.builder()
                                        .date(currentDate)
                                        .dayNumber(i + 1)
                                        .isStamped(isStamped)
                                        .isToday(currentDate.isEqual(today))
                                        .isLastDay(currentDate.isEqual(period.getEndDate()))
                                        .build());
                }

                return StampResponse.builder()
                                .period(StampResponse.PeriodInfoDto.builder()
                                                .id(period.getId())
                                                .startDate(period.getStartDate())
                                                .endDate(period.getEndDate())
                                                .build())
                                .navigation(navigation)
                                .today(today)
                                .stamps(stampDetails)
                                .totalDays(30)
                                .stampedDays(stampedDaysCount)
                                .build();
        }

        /**
         * 이전/다음 기간 탐색 정보 생성
         */
        private StampResponse.NavigationDto buildNavigation(Long memberId, BudgetPeriod currentPeriod) {
                // 회원의 모든 기간 조회 (시작일 오름차순)
                List<BudgetPeriod> allPeriods = budgetPeriodRepository.findAllByMemberIdOrderByStartDateAsc(memberId);

                Long previousPeriodId = null;
                Long nextPeriodId = null;

                for (int i = 0; i < allPeriods.size(); i++) {
                        if (allPeriods.get(i).getId().equals(currentPeriod.getId())) {
                                if (i > 0) {
                                        previousPeriodId = allPeriods.get(i - 1).getId();
                                }
                                if (i < allPeriods.size() - 1) {
                                        nextPeriodId = allPeriods.get(i + 1).getId();
                                }
                                break;
                        }
                }

                return StampResponse.NavigationDto.builder()
                                .hasPrevious(previousPeriodId != null)
                                .hasNext(nextPeriodId != null)
                                .previousPeriodId(previousPeriodId)
                                .nextPeriodId(nextPeriodId)
                                .build();
        }
}