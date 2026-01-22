package com.leets.monifit_be.domain.budget.entity;

import com.leets.monifit_be.domain.member.entity.Member;
import com.leets.monifit_be.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;

@Entity
@Table(name = "budget_period")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class BudgetPeriod extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member member;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "budget_amount", nullable = false)
    private Integer budgetAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PeriodStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "completion_type", length = 20)
    private CompletionType completionType;

    @Column(name = "warning_shown", nullable = false)
    private boolean warningShown;

    @Column(name = "over_budget_shown", nullable = false)
    private boolean overBudgetShown;

    @Column(name = "period_complete_shown", nullable = false)
    private boolean periodCompleteShown;

    @Builder
    public BudgetPeriod(Member member, LocalDate startDate, Integer budgetAmount) {
        this.member = member;
        this.startDate = startDate;
        this.endDate = startDate.plusDays(29);
        this.budgetAmount = budgetAmount;
        this.status = PeriodStatus.ACTIVE;
        this.warningShown = false;
        this.overBudgetShown = false;
        this.periodCompleteShown = false;
    }

    public void complete(CompletionType completionType) {
        this.status = PeriodStatus.COMPLETED;
        this.completionType = completionType;
    }

    public void showWarning() {
        this.warningShown = true;
    }

    public void showOverBudget() {
        this.overBudgetShown = true;
    }

    public void showPeriodComplete() {
        this.periodCompleteShown = true;
    }
}