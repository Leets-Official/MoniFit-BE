package com.leets.monifit_be.domain.member.dto;

import com.leets.monifit_be.domain.member.entity.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

/**
 * 회원 정보 응답 DTO
 * GET /members/me 응답에 사용
 *
 * 요구사항 9-1 마이페이지:
 * - 이메일: 카카오 계정 이메일 주소 표시 (수정 불가)
 * - 시작일: 최초 목표 예산 설정 시작일 표시
 * - 이름: 현재 이름 표시 (수정 가능)
 * - 사용 기간: 시작일로부터 현재까지의 기간
 * - 절약/초과 통계
 */
@Schema(description = "회원 정보 응답 (마이페이지)")
@Getter
@Builder
public class MemberResponse {

    @Schema(description = "회원 ID", example = "1")
    private Long id;

    @Schema(description = "회원 이름", example = "홍길동")
    private String name;

    @Schema(description = "카카오 이메일", example = "user@kakao.com")
    private String email;

    @Schema(description = "가입일", example = "2025-01-01T00:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "예산 기간 설정 이력 여부", example = "true")
    private boolean hasEverSetBudget;

    @Schema(description = "사용 기간 정보 (예산 설정 이력이 없으면 null)")
    private UsageInfo usage;

    @Schema(description = "활동 요약 (예산 설정 이력이 없으면 null)")
    private SummaryInfo summary;

    /**
     * 사용 기간 정보
     */
    @Schema(description = "사용 기간 정보")
    @Getter
    @Builder
    public static class UsageInfo {
        @Schema(description = "최초 예산 기간 시작일", example = "2025-01-01")
        private LocalDate startDate;

        @Schema(description = "사용 기간 (년)", example = "1")
        private int years;

        @Schema(description = "사용 기간 (개월)", example = "0")
        private int months;
    }

    /**
     * 활동 요약 정보
     */
    @Schema(description = "활동 요약 정보")
    @Getter
    @Builder
    public static class SummaryInfo {
        @Schema(description = "절약 달성 기간 횟수", example = "5")
        private int savedPeriodCount;

        @Schema(description = "예산 초과 기간 횟수", example = "2")
        private int exceededPeriodCount;

        @Schema(description = "총 누적 절약 금액 (원)", example = "150000")
        private int totalSavedAmount;
    }

    /**
     * Entity -> DTO 변환 (전체 정보)
     *
     * @param member               회원 엔티티
     * @param hasEverSetBudget     예산 설정 이력 여부
     * @param firstBudgetStartDate 최초 예산 기간 시작일 (없으면 null)
     * @param savedPeriodCount     절약 달성 기간 횟수
     * @param exceededPeriodCount  예산 초과 기간 횟수
     * @param totalSavedAmount     총 누적 절약 금액
     */
    public static MemberResponse from(
            Member member,
            boolean hasEverSetBudget,
            LocalDate firstBudgetStartDate,
            int savedPeriodCount,
            int exceededPeriodCount,
            int totalSavedAmount) {

        UsageInfo usageInfo = null;
        SummaryInfo summaryInfo = null;

        if (hasEverSetBudget && firstBudgetStartDate != null) {
            // 사용 기간 계산
            Period period = Period.between(firstBudgetStartDate, LocalDate.now());

            usageInfo = UsageInfo.builder()
                    .startDate(firstBudgetStartDate)
                    .years(period.getYears())
                    .months(period.getMonths())
                    .build();

            summaryInfo = SummaryInfo.builder()
                    .savedPeriodCount(savedPeriodCount)
                    .exceededPeriodCount(exceededPeriodCount)
                    .totalSavedAmount(totalSavedAmount)
                    .build();
        }

        return MemberResponse.builder()
                .id(member.getId())
                .name(member.getName())
                .email(member.getEmail())
                .createdAt(member.getCreatedAt())
                .hasEverSetBudget(hasEverSetBudget)
                .usage(usageInfo)
                .summary(summaryInfo)
                .build();
    }
}
