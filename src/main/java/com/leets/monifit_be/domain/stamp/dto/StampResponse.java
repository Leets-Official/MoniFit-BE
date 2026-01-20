package com.leets.monifit_be.domain.stamp.dto;

import java.time.LocalDate;
import java.util.List;

public class StampResponse {
    private List<LocalDate> stampedDates; // 도장이 찍힌 날짜 리스트

    public StampResponse(List<LocalDate> stampedDates) {
        this.stampedDates = stampedDates;
    }

    public List<LocalDate> getStampedDates() { return stampedDates; }
}