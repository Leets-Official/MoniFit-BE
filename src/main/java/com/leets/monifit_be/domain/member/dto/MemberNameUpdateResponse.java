package com.leets.monifit_be.domain.member.dto;

import com.leets.monifit_be.domain.member.entity.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

/**
 * 이름 수정 응답 DTO
 * PATCH /members/me/name 응답에 사용
 * 
 * API 명세서에 따라 간단한 정보만 반환
 */
@Schema(description = "이름 수정 응답")
@Getter
@Builder
public class MemberNameUpdateResponse {

    @Schema(description = "회원 ID", example = "1")
    private Long id;

    @Schema(description = "수정된 이름", example = "김모니")
    private String name;

    @Schema(description = "이메일", example = "user@kakao.com")
    private String email;

    /**
     * Entity -> DTO 변환
     */
    public static MemberNameUpdateResponse from(Member member) {
        return MemberNameUpdateResponse.builder()
                .id(member.getId())
                .name(member.getName())
                .email(member.getEmail())
                .build();
    }
}
