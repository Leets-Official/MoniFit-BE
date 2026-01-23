package com.leets.monifit_be.domain.auth.service;

import com.leets.monifit_be.domain.auth.dto.KakaoTokenResponse;
import com.leets.monifit_be.domain.auth.dto.KakaoUserInfo;
import com.leets.monifit_be.global.exception.BusinessException;
import com.leets.monifit_be.global.exception.ErrorCode;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * 카카오 OAuth API 호출 서비스
 */
@Slf4j
@Service
public class KakaoOAuthService {

    private final WebClient webClient;
    private final String clientId;
    private final String clientSecret;
    private final String redirectUri;
    private final String tokenUri;
    private final String userInfoUri;
    private final String adminKey;
    private final String unlinkUri;

    public KakaoOAuthService(
            @Value("${kakao.client-id}") String clientId,
            @Value("${kakao.client-secret}") String clientSecret,
            @Value("${kakao.redirect-uri}") String redirectUri,
            @Value("${kakao.token-uri}") String tokenUri,
            @Value("${kakao.user-info-uri}") String userInfoUri,
            @Value("${kakao.admin-key}") String adminKey,
            @Value("${kakao.unlink-uri}") String unlinkUri) {

        // 타임아웃 설정 (연결 10초, 읽기 10초)
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                .responseTimeout(Duration.ofSeconds(10))
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(10, TimeUnit.SECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(10, TimeUnit.SECONDS)));

        this.webClient = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;
        this.tokenUri = tokenUri;
        this.userInfoUri = userInfoUri;
        this.adminKey = adminKey;
        this.unlinkUri = unlinkUri;
    }

    /**
     * 카카오 인가 코드로 액세스 토큰 발급
     *
     * @param code 카카오 인가 코드
     * @return 카카오 액세스 토큰
     */
    public String getAccessToken(String code) {
        try {
            KakaoTokenResponse response = webClient.post()
                    .uri(tokenUri)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                    .bodyValue(buildTokenRequest(code))
                    .retrieve()
                    .bodyToMono(KakaoTokenResponse.class)
                    .block();

            if (response == null || response.getAccessToken() == null) {
                log.error("카카오 토큰 응답이 비어있습니다.");
                throw new BusinessException(ErrorCode.KAKAO_AUTH_FAILED);
            }

            log.info("카카오 액세스 토큰 발급 성공");
            return response.getAccessToken();

        } catch (WebClientResponseException e) {
            log.error("카카오 토큰 발급 실패: {}", e.getResponseBodyAsString());
            throw new BusinessException(ErrorCode.KAKAO_AUTH_FAILED);
        }
    }

    /**
     * 카카오 액세스 토큰으로 사용자 정보 조회
     *
     * @param accessToken 카카오 액세스 토큰
     * @return 카카오 사용자 정보
     */
    public KakaoUserInfo getUserInfo(String accessToken) {
        try {
            KakaoUserInfo userInfo = webClient.get()
                    .uri(userInfoUri)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                    .retrieve()
                    .bodyToMono(KakaoUserInfo.class)
                    .block();

            if (userInfo == null || userInfo.getId() == null) {
                log.error("카카오 사용자 정보 응답이 비어있습니다.");
                throw new BusinessException(ErrorCode.KAKAO_AUTH_FAILED);
            }

            log.info("카카오 사용자 정보 조회 성공: kakaoId={}", userInfo.getId());
            return userInfo;

        } catch (WebClientResponseException e) {
            log.error("카카오 사용자 정보 조회 실패: {}", e.getResponseBodyAsString());
            throw new BusinessException(ErrorCode.KAKAO_AUTH_FAILED);
        }
    }

    /**
     * 토큰 요청 바디 생성
     */
    private String buildTokenRequest(String code) {
        return "grant_type=authorization_code" +
                "&client_id=" + clientId +
                "&client_secret=" + clientSecret +
                "&redirect_uri=" + redirectUri +
                "&code=" + code;
    }

    /**
     * 카카오 사용자 연동 해제
     * Admin Key를 사용하여 서버에서 직접 연동 해제
     *
     * @param kakaoId 카카오 회원 고유 ID
     */
    public void unlinkUser(Long kakaoId) {
        try {
            webClient.post()
                    .uri(unlinkUri)
                    .header(HttpHeaders.AUTHORIZATION, "KakaoAK " + adminKey)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                    .bodyValue("target_id_type=user_id&target_id=" + kakaoId)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            log.info("카카오 연동 해제 성공: kakaoId={}", kakaoId);

        } catch (WebClientResponseException e) {
            log.error("카카오 연동 해제 실패: kakaoId={}, error={}", kakaoId, e.getResponseBodyAsString());
            throw new BusinessException(ErrorCode.KAKAO_UNLINK_FAILED);
        }
    }
}
