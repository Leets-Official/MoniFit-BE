package com.leets.monifit_be.domain.expense.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class StampResponse {

    private List<LocalDate> stampedDates;

    private LocalDate startDate;

    private LocalDate endDate;
    private LocalDate today;

    private boolean isLastDay;
}