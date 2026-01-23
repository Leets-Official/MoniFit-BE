package com.leets.monifit_be.domain.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

/**
 * 회원 이름 수정 요청 DTO
 * PATCH /members/me/name 요청에 사용
 */
@Getter
public class MemberNameUpdateRequest {

    @NotBlank(message = "이름은 필수입니다")
    @Size(max = 50, message = "이름은 50자 이하여야 합니다")
    private String name;
}
