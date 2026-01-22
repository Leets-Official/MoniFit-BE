package com.leets.monifit_be.domain.expense.service;

import com.leets.monifit_be.domain.budget.entity.BudgetPeriod;
import com.leets.monifit_be.domain.budget.entity.CompletionType;
import com.leets.monifit_be.domain.budget.entity.PeriodStatus;
import com.leets.monifit_be.domain.budget.repository.BudgetPeriodRepository;
import com.leets.monifit_be.domain.expense.dto.ExpenseCreateRequest;
import com.leets.monifit_be.domain.expense.dto.ExpenseCreateResponse;
import com.leets.monifit_be.domain.expense.entity.Expense;
import com.leets.monifit_be.domain.expense.repository.ExpenseRepository;
import com.leets.monifit_be.global.exception.ActiveBudgetNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final BudgetPeriodRepository budgetPeriodRepository;

    @Transactional
    public ExpenseCreateResponse createExpense(Long memberId, ExpenseCreateRequest request) {
        //  활성 예산 기간 조회
        BudgetPeriod budgetPeriod = budgetPeriodRepository.findByMemberIdAndStatus(memberId, PeriodStatus.ACTIVE)
                .orElseThrow(() -> new ActiveBudgetNotFoundException("활성화된 예산 기간이 없습니다."));

        // 날짜 설정 및 오늘 첫 기록 여부 확인
        LocalDate spentDate = (request.getSpentDate() != null) ? request.getSpentDate() : LocalDate.now();
        boolean isTodayRecord = spentDate.equals(LocalDate.now());
        boolean todayFirstExpense = !expenseRepository.existsByBudgetPeriodAndSpentDate(budgetPeriod, spentDate);

        // 지출 저장
        Expense expense = Expense.builder()
                .budgetPeriod(budgetPeriod)
                .category(request.getCategory())
                .amount(request.getAmount())
                .spentDate(spentDate)
                .build();
        expenseRepository.save(expense);

        // 예산 계산 및 상태 업데이트
        long totalExpense = expenseRepository.sumAmountByBudgetPeriod(budgetPeriod);
        long budgetAmount = budgetPeriod.getBudgetAmount();
        long remainingBudget = Math.max(0, budgetAmount - totalExpense);
        double usageRate = (double) totalExpense / budgetAmount * 100;

        // 예산 초과 감지 시 자동 종료
        boolean periodCompleted = totalExpense > budgetAmount;
        if (periodCompleted) {
            budgetPeriod.complete(CompletionType.OVER_BUDGET);
        }

        // 명세서 규격에 맞는 알림(Alerts) 구성
        String dateTitle = formatDateTitle(spentDate);

        // 50% 경고 알림 조건 (기간당 1회)
        boolean showWarning = usageRate >= 50.0 && !budgetPeriod.isWarningShown() && !periodCompleted;
        if (showWarning) budgetPeriod.showWarning();

        // 스탬프 알림 조건
        boolean showStamp = isTodayRecord && todayFirstExpense;

        //  명세서와 100% 일치하는 응답 DTO 조립
        return ExpenseCreateResponse.builder()
                .expense(ExpenseCreateResponse.ExpenseDto.builder()
                        .id(expense.getId())
                        .category(expense.getCategory().name())
                        .amount(expense.getAmount())
                        .spentDate(expense.getSpentDate())
                        .build())
                .periodCompleted(periodCompleted)
                .completionType(periodCompleted ? "OVER_BUDGET" : null)
                .alerts(ExpenseCreateResponse.AlertsDto.builder()
                        .expenseInput(ExpenseCreateResponse.AlertDetail.builder()
                                .title(dateTitle)
                                .message(expense.getCategory().name() + " " + expense.getAmount() + "원 지출 입력되었습니다")
                                .build())
                        .showStamp(showStamp)
                        .stamp(showStamp ? ExpenseCreateResponse.AlertDetail.builder()
                                .title(dateTitle)
                                .message("오늘 기록 완료! 스탬프가 찍혔어요 🎉")
                                .build() : null)
                        .showWarning(showWarning)
                        .showOverBudget(periodCompleted)
                        .build())
                .updatedBudget(periodCompleted ? null : ExpenseCreateResponse.UpdatedBudgetDto.builder()
                        .totalExpense((int) totalExpense)
                        .remainingBudget((int) remainingBudget)
                        .usageRate(usageRate)
                        .build())
                .build();
    }

    // 날짜를 "yy.MM.dd 요요일" 형식으로 변환하는 헬퍼 메서드
    private String formatDateTitle(LocalDate date) {
        String dayOfWeek = date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.KOREAN);
        return date.toString().substring(2).replace("-", ".") + " " + dayOfWeek;
    }
}