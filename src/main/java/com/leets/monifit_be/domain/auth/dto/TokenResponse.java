package com.leets.monifit_be.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

/**
 * 로그인/토큰 재발급 응답 DTO
 * API 명세서에 맞춰 정의
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

    /**
     * 로그인 응답 생성 (API 명세서 형식)
     */
    public static TokenResponse ofLogin(
            String accessToken,
            String refreshToken,
            int expiresInSeconds,
            boolean isNewMember,
            boolean hasEverSetBudget) {
        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(expiresInSeconds)
                .isNewMember(isNewMember)
                .hasEverSetBudget(hasEverSetBudget)
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
