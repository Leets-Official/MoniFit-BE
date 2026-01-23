package com.leets.monifit_be.domain.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 카카오 사용자 정보 API 응답
 * https://kapi.kakao.com/v2/user/me
 */
@Getter
@NoArgsConstructor
public class KakaoUserInfo {

    @JsonProperty("id")
    private Long id; // 카카오 회원번호 (kakaoId)

    @JsonProperty("connected_at")
    private String connectedAt;

    @JsonProperty("kakao_account")
    private KakaoAccount kakaoAccount;

    @Getter
    @NoArgsConstructor
    public static class KakaoAccount {

        @JsonProperty("profile")
        private Profile profile;

        @JsonProperty("email")
        private String email;

        @JsonProperty("is_email_valid")
        private Boolean isEmailValid;

        @JsonProperty("is_email_verified")
        private Boolean isEmailVerified;

        @Getter
        @NoArgsConstructor
        public static class Profile {

            @JsonProperty("nickname")
            private String nickname;

            @JsonProperty("profile_image_url")
            private String profileImageUrl;

            @JsonProperty("thumbnail_image_url")
            private String thumbnailImageUrl;

            @JsonProperty("is_default_image")
            private Boolean isDefaultImage;
        }
    }

    /**
     * 닉네임 추출 (없으면 "사용자" 반환)
     */
    public String getNickname() {
        if (kakaoAccount != null && kakaoAccount.getProfile() != null) {
            String nickname = kakaoAccount.getProfile().getNickname();
            return nickname != null ? nickname : "사용자";
        }
        return "사용자";
    }

    /**
     * 이메일 추출 (없으면 빈 문자열 반환)
     */
    public String getEmail() {
        if (kakaoAccount != null && kakaoAccount.getEmail() != null) {
            return kakaoAccount.getEmail();
        }
        return "";
    }
}
