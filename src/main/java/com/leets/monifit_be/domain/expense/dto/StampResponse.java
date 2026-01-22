package com.leets.monifit_be.domain.expense.dto;

import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StampResponse {
    private PeriodInfoDto period;
    private LocalDate today;
    private List<StampDetail> stamps;
    private Integer totalDays;
    private Integer stampedDays;

    @Getter
    @Builder
    public static class PeriodInfoDto {
        private Long id;
        private LocalDate startDate;
        private LocalDate endDate;
    }

    @Getter
    @Builder
    public static class StampDetail {
        private LocalDate date;
        private Integer dayNumber; // 1~30일차
        private boolean isStamped; // 스탬프 찍혔는지 여부
        private boolean isToday;   // 오늘인지 여부
        private boolean isLastDay; // 마지막 날인지 (선물 아이콘용)
    }
}