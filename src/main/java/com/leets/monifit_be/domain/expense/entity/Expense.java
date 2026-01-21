package com.leets.monifit_be.domain.expense.entity;

import com.leets.monifit_be.domain.budget.entity.BudgetPeriod;
import com.leets.monifit_be.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;

@Entity
@Table(name = "expense")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Expense extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "budget_period_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private BudgetPeriod budgetPeriod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ExpenseCategory category;

    @Column(nullable = false)
    private Integer amount;

    @Column(name = "spent_date", nullable = false)
    private LocalDate spentDate;

    @Builder
    private Expense(BudgetPeriod budgetPeriod, ExpenseCategory category, Integer amount, LocalDate spentDate) {
        this.budgetPeriod = budgetPeriod;
        this.category = category;
        this.amount = amount;
        this.spentDate = spentDate;
    }

    public void updateAmount(Integer amount) {
        this.amount = amount;
    }
}
