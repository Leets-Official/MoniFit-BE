package com.leets.monifit_be.domain.member.dto;

import com.leets.monifit_be.domain.member.entity.Member;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 회원 정보 응답 DTO
 * GET /members/me 응답에 사용
 *
 * 요구사항 9-1 마이페이지:
 * - 이메일: 카카오 계정 이메일 주소 표시 (수정 불가)
 * - 시작일: 최초 목표 예산 설정 시작일 표시
 * - 이름: 현재 이름 표시 (수정 가능)
 */
@Getter
@Builder
public class MemberResponse {

    private Long id;
    private String email;
    private String name;
    private LocalDateTime createdAt;
    private LocalDate firstBudgetStartDate; // 최초 목표 예산 설정 시작일

    /**
     * Entity -> DTO 변환
     *
     * @param member               회원 엔티티
     * @param firstBudgetStartDate 최초 예산 기간 시작일 (없으면 null)
     */
    public static MemberResponse from(Member member, LocalDate firstBudgetStartDate) {
        return MemberResponse.builder()
                .id(member.getId())
                .email(member.getEmail())
                .name(member.getName())
                .createdAt(member.getCreatedAt())
                .firstBudgetStartDate(firstBudgetStartDate)
                .build();
    }
}
