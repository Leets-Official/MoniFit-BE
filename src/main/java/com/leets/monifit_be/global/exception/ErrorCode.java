package com.leets.monifit_be.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Common (400~)
    INVALID_INPUT(400, "INVALID_INPUT", "잘못된 입력입니다"),
    UNAUTHORIZED(401, "UNAUTHORIZED", "인증이 필요합니다"),
    FORBIDDEN(403, "FORBIDDEN", "접근 권한이 없습니다"),
    RESOURCE_NOT_FOUND(404, "RESOURCE_NOT_FOUND", "리소스를 찾을 수 없습니다"),
    INTERNAL_SERVER_ERROR(500, "INTERNAL_SERVER_ERROR", "서버 오류가 발생했습니다"),

    // Auth
    INVALID_TOKEN(401, "INVALID_TOKEN", "유효하지 않은 리프레시 토큰입니다"),
    EXPIRED_TOKEN(401, "EXPIRED_TOKEN", "만료된 리프레시 토큰입니다"),
    INVALID_AUTHORIZATION_CODE(400, "INVALID_AUTHORIZATION_CODE", "유효하지 않은 인가 코드입니다"),
    KAKAO_AUTH_FAILED(500, "KAKAO_AUTH_FAILED", "카카오 로그인 처리 중 오류가 발생했습니다"),
    KAKAO_UNLINK_FAILED(500, "KAKAO_UNLINK_FAILED", "카카오 연동 해제에 실패했습니다"),

    // Member
    MEMBER_NOT_FOUND(404, "MEMBER_NOT_FOUND", "사용자를 찾을 수 없습니다"),
    DUPLICATE_MEMBER(409, "DUPLICATE_MEMBER", "이미 존재하는 사용자입니다"),

    // BudgetPeriod
    BUDGET_PERIOD_NOT_FOUND(404, "BUDGET_PERIOD_NOT_FOUND", "예산 기간을 찾을 수 없습니다"),
    ACTIVE_PERIOD_NOT_FOUND(404, "ACTIVE_PERIOD_NOT_FOUND", "활성화된 예산 기간이 없습니다"),
    ACTIVE_PERIOD_EXISTS(409, "ACTIVE_PERIOD_EXISTS", "이미 활성화된 예산 기간이 있습니다"),
    INVALID_START_DATE(400, "INVALID_START_DATE", "시작일은 오늘이어야 합니다"),

    // Expense
    EXPENSE_NOT_FOUND(404, "EXPENSE_NOT_FOUND", "지출 내역을 찾을 수 없습니다"),
    INVALID_EXPENSE_DATE(400, "INVALID_EXPENSE_DATE", "유효하지 않은 지출 날짜입니다");

    private final int status;
    private final String code;
    private final String message;
}
