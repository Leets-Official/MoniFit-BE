package com.leets.monifit_be.domain.expense.repository;

import com.leets.monifit_be.domain.budget.entity.BudgetPeriod;
import com.leets.monifit_be.domain.expense.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    // 월별 지출 날짜 조회
    @Query("SELECT DISTINCT e.spentDate FROM Expense e " +
            "WHERE e.budgetPeriod.member.id = :memberId " +
            "AND FUNCTION('YEAR', e.spentDate) = :year " +
            "AND FUNCTION('MONTH', e.spentDate) = :month")
    List<LocalDate> findDatesByMemberAndMonth(
            @Param("memberId") Long memberId,
            @Param("year") int year,
            @Param("month") int month
    );

    // 오늘 스탬프 여부 확인
    boolean existsByBudgetPeriodMemberIdAndSpentDate(Long memberId, LocalDate spentDate);

    // 특정 날짜의 본인 지출 내역 조회 (보안을 위해 memberId 추가 권장)
    List<Expense> findBySpentDateAndBudgetPeriodMemberId(LocalDate spentDate, Long memberId);

    // 특정 예산 기간의 총 지출 합계 계산 (추가 필수)
    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.budgetPeriod = :budgetPeriod")
    long sumAmountByBudgetPeriod(@Param("budgetPeriod") BudgetPeriod budgetPeriod);
}