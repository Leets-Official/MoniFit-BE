package com.leets.monifit_be.domain.auth.dto;

import com.leets.monifit_be.domain.member.entity.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

/**
 * 로그인/토큰 재발급 응답 DTO
 * API 명세서에 맞춰 모든 필드 포함
 */
@Schema(description = "토큰 응답")
@Getter
@Builder
public class TokenResponse {

    @Schema(description = "액세스 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String accessToken;

    @Schema(description = "리프레시 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String refreshToken;

    @Schema(description = "토큰 타입", example = "Bearer")
    private String tokenType;

    @Schema(description = "액세스 토큰 만료 시간(초)", example = "3600")
    private Integer expiresIn;

    @Schema(description = "신규 회원 여부 (카카오 첫 로그인)")
    private Boolean isNewMember;

    @Schema(description = "예산 기간 설정 이력 여부 (첫 설정 시 '환영해요!' 메시지 표시용)")
    private Boolean hasEverSetBudget;

    @Schema(description = "회원 정보")
    private MemberInfo member;

    /**
     * 회원 정보 내부 DTO
     */
    @Schema(description = "회원 기본 정보")
    @Getter
    @Builder
    public static class MemberInfo {

        @Schema(description = "회원 ID", example = "1")
        private Long id;

        @Schema(description = "회원 이름", example = "홍길동")
        private String name;

        @Schema(description = "이메일", example = "user@kakao.com")
        private String email;

        public static MemberInfo from(Member member) {
            return MemberInfo.builder()
                    .id(member.getId())
                    .name(member.getName())
                    .email(member.getEmail())
                    .build();
        }
    }

    /**
     * 로그인 응답 생성 (전체 정보 포함)
     */
    public static TokenResponse ofLogin(
            String accessToken,
            String refreshToken,
            int expiresInSeconds,
            boolean isNewMember,
            boolean hasEverSetBudget,
            Member member) {
        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(expiresInSeconds)
                .isNewMember(isNewMember)
                .hasEverSetBudget(hasEverSetBudget)
                .member(MemberInfo.from(member))
                .build();
    }

    /**
     * 토큰 재발급 응답 생성 (토큰 정보만)
     */
    public static TokenResponse ofReissue(
            String accessToken,
            String refreshToken,
            int expiresInSeconds) {
        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(expiresInSeconds)
                .build();
    }
}
