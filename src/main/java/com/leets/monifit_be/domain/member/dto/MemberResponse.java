package com.leets.monifit_be.domain.member.dto;

import com.leets.monifit_be.domain.member.entity.Member;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 회원 정보 응답 DTO
 * GET /members/me 응답에 사용
 */
@Getter
@Builder
public class MemberResponse {

    private Long id;
    private String email;
    private String name;
    private LocalDateTime createdAt;

    /**
     * Entity -> DTO 변환
     */
    public static MemberResponse from(Member member) {
        return MemberResponse.builder()
                .id(member.getId())
                .email(member.getEmail())
                .name(member.getName())
                .createdAt(member.getCreatedAt())
                .build();
    }
}
