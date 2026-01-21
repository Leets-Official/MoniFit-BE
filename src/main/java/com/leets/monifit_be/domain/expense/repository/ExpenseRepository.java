package com.leets.monifit_be.domain.expense.repository;

import com.leets.monifit_be.domain.budget.entity.BudgetPeriod;
import com.leets.monifit_be.domain.expense.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    // 1. 월별 지출 날짜 조회
    @Query("SELECT DISTINCT e.spentDate FROM Expense e " +
            "WHERE e.budgetPeriod.member.id = :memberId " +
            "AND FUNCTION('YEAR', e.spentDate) = :year " +
            "AND FUNCTION('MONTH', e.spentDate) = :month")
    List<LocalDate> findDatesByMonth(
            @Param("memberId") Long memberId,
            @Param("year") int year,
            @Param("month") int month
    );

    // 2. 서비스 에러 해결을 위해 추가: 특정 예산 기간 내 해당 날짜 지출 여부 확인
    boolean existsByBudgetPeriodAndSpentDate(BudgetPeriod budgetPeriod, LocalDate spentDate);

    // 3. 오늘 스탬프 여부 확인
    boolean existsByBudgetPeriodMemberIdAndSpentDate(Long memberId, LocalDate spentDate);

    // 4. 특정 날짜의 본인 지출 내역 조회
    List<Expense> findBySpentDateAndBudgetPeriodMemberId(LocalDate spentDate, Long memberId);

    // 5. 특정 예산 기간의 총 지출 합계 계산
    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.budgetPeriod = :budgetPeriod")
    long sumAmountByBudgetPeriod(@Param("budgetPeriod") BudgetPeriod budgetPeriod);
}