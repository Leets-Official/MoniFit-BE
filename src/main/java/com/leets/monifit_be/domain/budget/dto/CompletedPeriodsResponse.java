package com.leets.monifit_be.domain.budget.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * 완료된 기간 목록 응답 DTO
 * GET /budget-periods/completed 응답에 사용
 */
@Schema(description = "완료된 기간 목록 응답")
@Getter
@Builder
public class CompletedPeriodsResponse {

    @Schema(description = "완료된 기간 목록")
    private List<CompletedPeriodItem> periods;

    @Schema(description = "총 완료 기간 수", example = "2")
    private Integer totalCount;

    public static CompletedPeriodsResponse of(List<CompletedPeriodItem> periods) {
        return CompletedPeriodsResponse.builder()
                .periods(periods)
                .totalCount(periods.size())
                .build();
    }
}
