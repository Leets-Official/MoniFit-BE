package com.leets.monifit_be.domain.auth.controller;

import com.leets.monifit_be.domain.auth.dto.KakaoLoginRequest;
import com.leets.monifit_be.domain.auth.dto.ReissueRequest;
import com.leets.monifit_be.domain.auth.dto.TokenResponse;
import com.leets.monifit_be.domain.auth.service.AuthService;
import com.leets.monifit_be.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Tag(name = "Auth", description = "인증 API")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 카카오 로그인 (회원가입 포함)
     * 인증 없이 접근 가능
     */
    @Operation(summary = "카카오 로그인", description = "카카오 인가 코드로 로그인합니다. 신규 사용자는 자동 회원가입됩니다.")
    @PostMapping("/kakao/login")
    public ResponseEntity<ApiResponse<TokenResponse>> kakaoLogin(
            @Valid @RequestBody KakaoLoginRequest request) {

        TokenResponse response = authService.kakaoLogin(request.getAuthorizationCode());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 토큰 재발급
     * 인증 없이 접근 가능 (리프레시 토큰으로 인증)
     */
    @Operation(summary = "토큰 재발급", description = "리프레시 토큰으로 새로운 액세스 토큰과 리프레시 토큰을 발급받습니다.")
    @PostMapping("/reissue")
    public ResponseEntity<ApiResponse<TokenResponse>> reissue(
            @Valid @RequestBody ReissueRequest request) {

        TokenResponse response = authService.reissue(request.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 로그아웃
     * 인증 필요 (Authorization: Bearer {accessToken})
     */
    @Operation(summary = "로그아웃", description = "리프레시 토큰을 삭제하여 로그아웃합니다.")
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Object>> logout(
            @AuthenticationPrincipal Long memberId) {

        authService.logout(memberId);
        return ResponseEntity.ok(ApiResponse.success(Map.of("message", "로그아웃 되었습니다")));
    }
}
