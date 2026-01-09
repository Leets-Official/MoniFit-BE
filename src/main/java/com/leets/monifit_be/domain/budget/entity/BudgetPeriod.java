package com.leets.monifit_be.domain.budget.entity;

import com.leets.monifit_be.domain.member.entity.Member;
import com.leets.monifit_be.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "budget_period")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BudgetPeriod extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
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
    private Boolean warningShown;

    @Builder
    private BudgetPeriod(Member member, LocalDate startDate, Integer budgetAmount) {
        this.member = member;
        this.startDate = startDate;
        this.endDate = startDate.plusDays(29); // 30일 기간
        this.budgetAmount = budgetAmount;
        this.status = PeriodStatus.ACTIVE;
        this.warningShown = false;
    }

    public void complete(CompletionType completionType) {
        this.status = PeriodStatus.COMPLETED;
        this.completionType = completionType;
    }

    public void showWarning() {
        this.warningShown = true;
    }
}
