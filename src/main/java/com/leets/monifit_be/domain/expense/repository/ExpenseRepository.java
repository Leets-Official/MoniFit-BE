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

    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.budgetPeriod = :budgetPeriod")
    long sumAmountByBudgetPeriod(@Param("budgetPeriod") BudgetPeriod budgetPeriod);

    @Query("SELECT COUNT(e) > 0 FROM Expense e WHERE e.budgetPeriod.id = :periodId " +
            "AND e.spentDate = :date AND CAST(e.createdAt AS date) = :date")
    boolean existsValidStamp(@Param("periodId") Long periodId, @Param("date") LocalDate date);

    boolean existsByBudgetPeriodAndSpentDate(BudgetPeriod budgetPeriod, LocalDate spentDate);

    List<Expense> findByBudgetPeriodAndSpentDate(BudgetPeriod budgetPeriod, LocalDate spentDate);

    @Query("SELECT e FROM Expense e WHERE e.budgetPeriod.member.id = :memberId " +
            "AND YEAR(e.spentDate) = :year AND MONTH(e.spentDate) = :month")
    List<Expense> findAllByMemberAndMonth(@Param("memberId") Long memberId, @Param("year") int year, @Param("month") int month);
    List<Expense> findByBudgetPeriodId(Long budgetPeriodId);
}