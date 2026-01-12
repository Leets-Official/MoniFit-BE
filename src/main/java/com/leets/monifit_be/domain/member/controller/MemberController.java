package com.leets.monifit_be.domain.member.controller;

import com.leets.monifit_be.domain.member.dto.MemberNameUpdateRequest;
import com.leets.monifit_be.domain.member.dto.MemberResponse;
import com.leets.monifit_be.domain.member.service.MemberService;
import com.leets.monifit_be.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * 회원 API 컨트롤러
 * - GET /members/me: 내 정보 조회 (마이페이지)
 * - PATCH /members/me/name: 이름 수정
 * - DELETE /members/me: 계정 삭제 (탈퇴, 카카오 연동 해제 포함)
 */
@Tag(name = "Member", description = "회원 API")
@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    /**
     * 내 정보 조회 (마이페이지)
     * 인증 필요 (Authorization: Bearer {accessToken})
     */
    @Operation(summary = "내 정보 조회", description = "로그인한 사용자의 정보를 조회합니다 (마이페이지)")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<MemberResponse>> getMyInfo(
            @AuthenticationPrincipal Long memberId) {

        MemberResponse response = memberService.getMyInfo(memberId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 이름 수정
     * 인증 필요 (Authorization: Bearer {accessToken})
     */
    @Operation(summary = "이름 수정", description = "로그인한 사용자의 이름을 수정합니다")
    @PatchMapping("/me/name")
    public ResponseEntity<ApiResponse<MemberResponse>> updateName(
            @AuthenticationPrincipal Long memberId,
            @Valid @RequestBody MemberNameUpdateRequest request) {

        MemberResponse response = memberService.updateName(memberId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 계정 삭제 (탈퇴)
     * 카카오 연동 해제 포함
     * 인증 필요 (Authorization: Bearer {accessToken})
     */
    @Operation(summary = "계정 삭제", description = "회원 탈퇴합니다. 카카오 연동 해제가 함께 진행됩니다")
    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<Void>> deleteMember(
            @AuthenticationPrincipal Long memberId) {

        memberService.deleteMember(memberId);
        return ResponseEntity.ok(ApiResponse.success());
    }
}
