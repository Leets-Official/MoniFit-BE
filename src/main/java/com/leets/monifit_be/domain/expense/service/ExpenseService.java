package com.leets.monifit_be.domain.expense.service;

import com.leets.monifit_be.domain.budget.entity.BudgetPeriod;
import com.leets.monifit_be.domain.budget.entity.CompletionType;
import com.leets.monifit_be.domain.budget.entity.PeriodStatus;
import com.leets.monifit_be.domain.budget.repository.BudgetPeriodRepository;
import com.leets.monifit_be.domain.expense.dto.ExpenseCreateRequest;
import com.leets.monifit_be.domain.expense.dto.ExpenseCreateResponse;
import com.leets.monifit_be.domain.expense.dto.ExpenseDeleteResponse;
import com.leets.monifit_be.domain.expense.dto.ExpenseListResponse;
import com.leets.monifit_be.domain.expense.dto.ExpenseUpdateRequest;
import com.leets.monifit_be.domain.expense.dto.ExpenseUpdateResponse;
import com.leets.monifit_be.domain.expense.entity.Expense;
import com.leets.monifit_be.domain.expense.repository.ExpenseRepository;
import com.leets.monifit_be.global.exception.BusinessException;
import com.leets.monifit_be.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class ExpenseService {

        private final ExpenseRepository expenseRepository;
        private final BudgetPeriodRepository budgetPeriodRepository;

        @Transactional
        public ExpenseCreateResponse createExpense(Long memberId, ExpenseCreateRequest request) {
                // 1. 활성 예산 기간 조회
                BudgetPeriod budgetPeriod = budgetPeriodRepository
                                .findByMemberIdAndStatus(memberId, PeriodStatus.ACTIVE)
                                .orElseThrow(() -> new BusinessException(ErrorCode.ACTIVE_PERIOD_NOT_FOUND));

                // 2. 날짜 설정 및 오늘 첫 기록 여부 확인
                LocalDate spentDate = (request.getSpentDate() != null) ? request.getSpentDate() : LocalDate.now();
                boolean isTodayRecord = spentDate.equals(LocalDate.now());
                boolean todayFirstExpense = !expenseRepository.existsByBudgetPeriodAndSpentDate(budgetPeriod,
                                spentDate);

                // 3. 지출 날짜 유효성 검증 (활성 기간 내)
                if (spentDate.isBefore(budgetPeriod.getStartDate()) || spentDate.isAfter(budgetPeriod.getEndDate())) {
                        throw new BusinessException(ErrorCode.INVALID_EXPENSE_DATE);
                }

                // 4. 지출 저장
                Expense expense = Expense.builder()
                                .budgetPeriod(budgetPeriod)
                                .category(request.getCategory())
                                .amount(request.getAmount())
                                .spentDate(spentDate)
                                .build();
                expenseRepository.save(expense);

                // 5. 예산 계산 및 상태 업데이트
                long totalExpense = expenseRepository.sumAmountByBudgetPeriod(budgetPeriod);
                int budgetAmount = budgetPeriod.getBudgetAmount();
                long remainingBudget = Math.max(0, budgetAmount - totalExpense);
                double usageRate = (double) totalExpense / budgetAmount * 100;
                long remainingDays = Math.max(1,
                                java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), budgetPeriod.getEndDate()));
                long dailyRecommended = remainingBudget / remainingDays;

                // 6. 예산 초과 감지 시 자동 종료
                boolean periodCompleted = totalExpense > budgetAmount;
                Integer exceededAmount = null;
                if (periodCompleted) {
                        budgetPeriod.complete(CompletionType.OVER_BUDGET);
                        exceededAmount = (int) (totalExpense - budgetAmount);
                }

                // 7. 명세서 규격에 맞는 알림(Alerts) 구성
                String dateTitle = formatDateTitle(spentDate);

                // 50% 경고 알림 조건 (기간당 1회)
                boolean showWarning = usageRate >= 50.0 && !budgetPeriod.getWarningShown() && !periodCompleted;
                ExpenseCreateResponse.AlertDetail warningDetail = null;
                if (showWarning) {
                        budgetPeriod.showWarning();
                        warningDetail = ExpenseCreateResponse.AlertDetail.builder()
                                        .title("예산의 50%를 초과했어요 ⚠")
                                        .message("남은 기간 동안 하루 ₩" + String.format("%,d", dailyRecommended)
                                                        + "으로 조절하면 목표 달성이 가능해요")
                                        .build();
                }

                // 스탬프 알림 조건 (당일 기록 + 오늘 첫 기록)
                boolean showStamp = isTodayRecord && todayFirstExpense;

                // 예산 초과 알림
                ExpenseCreateResponse.AlertDetail overBudgetDetail = null;
                if (periodCompleted) {
                        overBudgetDetail = ExpenseCreateResponse.AlertDetail.builder()
                                        .title("예산을 ₩" + String.format("%,d", exceededAmount) + " 초과했어요 ⚠")
                                        .message("다음 기간엔 소비 속도를 조절해봐요")
                                        .build();
                }

                // 8. 명세서와 100% 일치하는 응답 DTO 조립
                return ExpenseCreateResponse.builder()
                                .expense(ExpenseCreateResponse.ExpenseDto.builder()
                                                .id(expense.getId())
                                                .category(expense.getCategory().name())
                                                .categoryName(expense.getCategory().getDisplayName())
                                                .amount(expense.getAmount())
                                                .spentDate(expense.getSpentDate())
                                                .createdAt(LocalDateTime.now().toString())
                                                .build())
                                .periodCompleted(periodCompleted)
                                .completionType(periodCompleted ? "OVER_BUDGET" : null)
                                .exceededAmount(exceededAmount)
                                .alerts(ExpenseCreateResponse.AlertsDto.builder()
                                                .expenseInput(ExpenseCreateResponse.AlertDetail.builder()
                                                                .title(dateTitle)
                                                                .message(expense.getCategory().getDisplayName() + " " +
                                                                                String.format("%,d",
                                                                                                expense.getAmount())
                                                                                + "원 지출 입력되었습니다")
                                                                .build())
                                                .showStamp(showStamp)
                                                .stamp(showStamp ? ExpenseCreateResponse.AlertDetail.builder()
                                                                .title(dateTitle)
                                                                .message("오늘 기록 완료! 스탬프가 찍혔어요 🎉")
                                                                .build() : null)
                                                .showWarning(showWarning)
                                                .warning(warningDetail)
                                                .showOverBudget(periodCompleted)
                                                .overBudget(overBudgetDetail)
                                                .build())
                                .updatedBudget(periodCompleted ? null
                                                : ExpenseCreateResponse.UpdatedBudgetDto.builder()
                                                                .totalExpense((int) totalExpense)
                                                                .remainingBudget((int) remainingBudget)
                                                                .usageRate(Math.round(usageRate * 10.0) / 10.0)
                                                                .build())
                                .build();
        }

        /**
         * 날짜를 "yy.MM.dd 요일" 형식으로 변환
         */
        private String formatDateTitle(LocalDate date) {
                String dayOfWeek = date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.KOREAN);
                String dateStr = String.format("%02d.%02d.%02d",
                                date.getYear() % 100, date.getMonthValue(), date.getDayOfMonth());
                return dateStr + " " + dayOfWeek;
        }

        /**
         * 지출 목록 조회 (API 명세서 5-2)
         */
        @Transactional(readOnly = true)
        public ExpenseListResponse getExpenses(Long memberId, Long periodId, LocalDate date, String category) {
                BudgetPeriod budgetPeriod;

                if (periodId != null) {
                        budgetPeriod = budgetPeriodRepository.findById(periodId)
                                        .orElseThrow(() -> new BusinessException(ErrorCode.BUDGET_PERIOD_NOT_FOUND));
                        if (!budgetPeriod.getMember().getId().equals(memberId)) {
                                throw new BusinessException(ErrorCode.FORBIDDEN);
                        }
                } else {
                        budgetPeriod = budgetPeriodRepository.findByMemberIdAndStatus(memberId, PeriodStatus.ACTIVE)
                                        .orElseThrow(() -> new BusinessException(ErrorCode.ACTIVE_PERIOD_NOT_FOUND));
                }

                List<Expense> expenses = expenseRepository.findByBudgetPeriodId(budgetPeriod.getId());

                // 필터링
                if (date != null) {
                        expenses = expenses.stream()
                                        .filter(e -> e.getSpentDate().isEqual(date))
                                        .collect(java.util.stream.Collectors.toList());
                }
                if (category != null && !category.isEmpty()) {
                        expenses = expenses.stream()
                                        .filter(e -> e.getCategory().name().equals(category))
                                        .collect(java.util.stream.Collectors.toList());
                }

                // 최신순 정렬
                expenses.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));

                List<ExpenseListResponse.ExpenseItem> items = expenses.stream()
                                .map(e -> ExpenseListResponse.ExpenseItem.builder()
                                                .id(e.getId())
                                                .category(e.getCategory().name())
                                                .categoryName(e.getCategory().getDisplayName())
                                                .amount(e.getAmount())
                                                .spentDate(e.getSpentDate())
                                                .createdAt(e.getCreatedAt().toString())
                                                .build())
                                .collect(java.util.stream.Collectors.toList());

                int totalAmount = items.stream().mapToInt(ExpenseListResponse.ExpenseItem::getAmount).sum();

                return ExpenseListResponse.builder()
                                .expenses(items)
                                .totalCount(items.size())
                                .totalAmount(totalAmount)
                                .build();
        }

        /**
         * 지출 수정 (API 명세서 5-3)
         */
        @Transactional
        public ExpenseUpdateResponse updateExpense(Long memberId, Long expenseId, ExpenseUpdateRequest request) {
                Expense expense = expenseRepository.findById(expenseId)
                                .orElseThrow(() -> new BusinessException(ErrorCode.EXPENSE_NOT_FOUND));

                BudgetPeriod budgetPeriod = expense.getBudgetPeriod();
                if (!budgetPeriod.getMember().getId().equals(memberId)) {
                        throw new BusinessException(ErrorCode.FORBIDDEN);
                }

                // 금액 수정
                expense.updateAmount(request.getAmount());

                // 예산 재계산
                long totalExpense = expenseRepository.sumAmountByBudgetPeriod(budgetPeriod);
                int budgetAmount = budgetPeriod.getBudgetAmount();
                long remainingBudget = Math.max(0, budgetAmount - totalExpense);
                double usageRate = (double) totalExpense / budgetAmount * 100;
                long remainingDays = Math.max(1,
                                java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), budgetPeriod.getEndDate()));
                long dailyRecommended = remainingBudget / remainingDays;

                // 예산 초과 감지
                boolean periodCompleted = totalExpense > budgetAmount;
                Integer exceededAmount = null;
                if (periodCompleted) {
                        budgetPeriod.complete(CompletionType.OVER_BUDGET);
                        exceededAmount = (int) (totalExpense - budgetAmount);
                }

                // 50% 경고 알림
                boolean showWarning = usageRate >= 50.0 && !budgetPeriod.getWarningShown() && !periodCompleted;
                ExpenseUpdateResponse.AlertDetail warningDetail = null;
                if (showWarning) {
                        budgetPeriod.showWarning();
                        warningDetail = ExpenseUpdateResponse.AlertDetail.builder()
                                        .title("예산의 50%를 초과했어요 ⚠")
                                        .message("남은 기간 동안 하루 ₩" + String.format("%,d", dailyRecommended)
                                                        + "으로 조절하면 목표 달성이 가능해요")
                                        .dailyRecommendedExpense((int) dailyRecommended)
                                        .build();
                }

                // 예산 초과 알림
                ExpenseUpdateResponse.AlertDetail overBudgetDetail = null;
                if (periodCompleted) {
                        overBudgetDetail = ExpenseUpdateResponse.AlertDetail.builder()
                                        .title("예산을 ₩" + String.format("%,d", exceededAmount) + " 초과했어요 ⚠")
                                        .message("다음 기간엔 소비 속도를 조절해봐요")
                                        .exceededAmount(exceededAmount)
                                        .build();
                }

                return ExpenseUpdateResponse.builder()
                                .expense(ExpenseUpdateResponse.ExpenseDto.builder()
                                                .id(expense.getId())
                                                .category(expense.getCategory().name())
                                                .categoryName(expense.getCategory().getDisplayName())
                                                .amount(expense.getAmount())
                                                .spentDate(expense.getSpentDate())
                                                .createdAt(expense.getCreatedAt().toString())
                                                .updatedAt(java.time.LocalDateTime.now().toString())
                                                .build())
                                .periodCompleted(periodCompleted)
                                .completionType(periodCompleted ? "OVER_BUDGET" : null)
                                .exceededAmount(exceededAmount)
                                .alerts(ExpenseUpdateResponse.AlertsDto.builder()
                                                .showWarning(showWarning)
                                                .warning(warningDetail)
                                                .showOverBudget(periodCompleted)
                                                .overBudget(overBudgetDetail)
                                                .build())
                                .updatedBudget(periodCompleted ? null
                                                : ExpenseUpdateResponse.UpdatedBudgetDto.builder()
                                                                .totalExpense((int) totalExpense)
                                                                .remainingBudget((int) remainingBudget)
                                                                .usageRate(Math.round(usageRate * 10.0) / 10.0)
                                                                .build())
                                .build();
        }

        /**
         * 지출 삭제 (API 명세서 5-4)
         */
        @Transactional
        public ExpenseDeleteResponse deleteExpense(Long memberId, Long expenseId) {
                Expense expense = expenseRepository.findById(expenseId)
                                .orElseThrow(() -> new BusinessException(ErrorCode.EXPENSE_NOT_FOUND));

                BudgetPeriod budgetPeriod = expense.getBudgetPeriod();
                if (!budgetPeriod.getMember().getId().equals(memberId)) {
                        throw new BusinessException(ErrorCode.FORBIDDEN);
                }

                expenseRepository.delete(expense);

                // 예산 재계산
                long totalExpense = expenseRepository.sumAmountByBudgetPeriod(budgetPeriod);
                int budgetAmount = budgetPeriod.getBudgetAmount();
                long remainingBudget = Math.max(0, budgetAmount - totalExpense);
                double usageRate = (budgetAmount > 0) ? (double) totalExpense / budgetAmount * 100 : 0;

                return ExpenseDeleteResponse.builder()
                                .updatedBudget(ExpenseDeleteResponse.UpdatedBudgetDto.builder()
                                                .totalExpense((int) totalExpense)
                                                .remainingBudget((int) remainingBudget)
                                                .usageRate(Math.round(usageRate * 10.0) / 10.0)
                                                .build())
                                .build();
        }
}