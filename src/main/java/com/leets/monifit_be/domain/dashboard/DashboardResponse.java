package com.leets.monifit_be.domain.budget.dto.response;

public class DashboardResponse {
    private Long remainingBudget;
    private Long totalExpense;
    private Double usageRate;
    private Integer remainingDays;
    private Long dailyRecommended;

    private boolean is50PercentWarning;
    private boolean isOverBudget;
    private boolean isPeriodEnded;

    public DashboardResponse(Long remainingBudget, Long totalExpense, Double usageRate,
                             Integer remainingDays, Long dailyRecommended,
                             boolean is50PercentWarning, boolean isOverBudget, boolean isPeriodEnded) {
        this.remainingBudget = remainingBudget;
        this.totalExpense = totalExpense;
        this.usageRate = usageRate;
        this.remainingDays = remainingDays;
        this.dailyRecommended = dailyRecommended;
        this.is50PercentWarning = is50PercentWarning;
        this.isOverBudget = isOverBudget;
        this.isPeriodEnded = isPeriodEnded;
    }

    // Getters
    public Long getRemainingBudget() { return remainingBudget; }
    public Long getTotalExpense() { return totalExpense; }
    public Double getUsageRate() { return usageRate; }
    public Integer getRemainingDays() { return remainingDays; }
    public Long getDailyRecommended() { return dailyRecommended; }
    public boolean isIs50PercentWarning() { return is50PercentWarning; }
    public boolean isIsOverBudget() { return isOverBudget; }
    public boolean isIsPeriodEnded() { return isPeriodEnded; }
}