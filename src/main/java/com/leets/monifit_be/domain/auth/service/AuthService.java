package com.leets.monifit_be.domain.auth.service;

import com.leets.monifit_be.domain.auth.dto.KakaoUserInfo;
import com.leets.monifit_be.domain.auth.dto.TokenResponse;
import com.leets.monifit_be.domain.auth.entity.RefreshToken;
import com.leets.monifit_be.domain.auth.repository.RefreshTokenRepository;
import com.leets.monifit_be.domain.budget.repository.BudgetPeriodRepository;
import com.leets.monifit_be.domain.member.entity.Member;
import com.leets.monifit_be.domain.member.repository.MemberRepository;
import com.leets.monifit_be.global.exception.BusinessException;
import com.leets.monifit_be.global.exception.ErrorCode;
import com.leets.monifit_be.global.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 인증 서비스
 * 카카오 로그인, 토큰 재발급, 로그아웃 처리
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final KakaoOAuthService kakaoOAuthService;
    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final BudgetPeriodRepository budgetPeriodRepository;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 카카오 로그인 (회원가입 포함)
     *
     * @param authorizationCode 카카오 인가 코드
     * @return JWT 토큰 쌍 및 회원 정보
     */
    @Transactional
    public TokenResponse kakaoLogin(String authorizationCode) {
        // 1. 카카오 인가 코드로 액세스 토큰 발급
        String kakaoAccessToken = kakaoOAuthService.getAccessToken(authorizationCode);

        // 2. 카카오 액세스 토큰으로 사용자 정보 조회
        KakaoUserInfo kakaoUserInfo = kakaoOAuthService.getUserInfo(kakaoAccessToken);

        // 3. 회원 조회 또는 신규 가입 (신규 여부 함께 반환)
        MemberResult memberResult = findOrCreateMember(kakaoUserInfo);
        Member member = memberResult.member;
        boolean isNewMember = memberResult.isNew;

        // 4. JWT 토큰 발급
        String accessToken = jwtTokenProvider.createAccessToken(member.getId());
        String refreshToken = jwtTokenProvider.createRefreshToken(member.getId());

        // 5. 리프레시 토큰 저장 (기존 토큰이 있으면 갱신)
        saveOrUpdateRefreshToken(member, refreshToken);

        // 6. 예산 기간 설정 이력 확인
        boolean hasEverSetBudget = budgetPeriodRepository.existsByMemberId(member.getId());

        log.info("카카오 로그인 성공: memberId={}, kakaoId={}, isNewMember={}",
                member.getId(), kakaoUserInfo.getId(), isNewMember);

        return TokenResponse.ofLogin(
                accessToken,
                refreshToken,
                (int) (jwtTokenProvider.getAccessTokenExpiration() / 1000),
                isNewMember,
                hasEverSetBudget);
    }

    /**
     * 토큰 재발급
     *
     * @param refreshTokenValue 리프레시 토큰
     * @return 새로운 JWT 토큰 쌍
     */
    @Transactional
    public TokenResponse reissue(String refreshTokenValue) {
        // 1. 리프레시 토큰 유효성 검증
        if (!jwtTokenProvider.validateToken(refreshTokenValue)) {
            log.warn("유효하지 않은 리프레시 토큰");
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }

        // 2. DB에서 리프레시 토큰 조회
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenValue)
                .orElseThrow(() -> {
                    log.warn("DB에 존재하지 않는 리프레시 토큰");
                    return new BusinessException(ErrorCode.INVALID_TOKEN);
                });

        // 3. 만료 시간 확인
        if (refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            log.warn("만료된 리프레시 토큰: memberId={}", refreshToken.getMember().getId());
            throw new BusinessException(ErrorCode.EXPIRED_TOKEN);
        }

        // 4. 새로운 토큰 발급
        Member member = refreshToken.getMember();
        String newAccessToken = jwtTokenProvider.createAccessToken(member.getId());
        String newRefreshToken = jwtTokenProvider.createRefreshToken(member.getId());

        // 5. 리프레시 토큰 갱신 (Rotation)
        refreshToken.updateToken(
                newRefreshToken,
                LocalDateTime.now().plusSeconds(jwtTokenProvider.getRefreshTokenExpiration() / 1000));

        log.info("토큰 재발급 성공: memberId={}", member.getId());

        return TokenResponse.ofReissue(
                newAccessToken,
                newRefreshToken,
                (int) (jwtTokenProvider.getAccessTokenExpiration() / 1000));
    }

    /**
     * 로그아웃
     *
     * @param memberId 회원 ID
     */
    @Transactional
    public void logout(Long memberId) {
        // 리프레시 토큰 삭제
        refreshTokenRepository.deleteByMemberId(memberId);
        log.info("로그아웃 성공: memberId={}", memberId);
    }

    /**
     * 회원 조회 또는 신규 가입 결과를 담는 내부 클래스
     */
    private record MemberResult(Member member, boolean isNew) {
    }

    /**
     * 회원 조회 또는 신규 가입
     */
    private MemberResult findOrCreateMember(KakaoUserInfo kakaoUserInfo) {
        return memberRepository.findByKakaoId(kakaoUserInfo.getId())
                .map(member -> new MemberResult(member, false))
                .orElseGet(() -> {
                    log.info("신규 회원 가입: kakaoId={}", kakaoUserInfo.getId());
                    Member newMember = Member.builder()
                            .kakaoId(kakaoUserInfo.getId())
                            .email(kakaoUserInfo.getEmail())
                            .name(kakaoUserInfo.getNickname())
                            .build();
                    return new MemberResult(memberRepository.save(newMember), true);
                });
    }

    /**
     * 리프레시 토큰 저장 또는 갱신
     */
    private void saveOrUpdateRefreshToken(Member member, String token) {
        LocalDateTime expiresAt = LocalDateTime.now()
                .plusSeconds(jwtTokenProvider.getRefreshTokenExpiration() / 1000);

        refreshTokenRepository.findByMember(member)
                .ifPresentOrElse(
                        // 기존 토큰이 있으면 갱신
                        existingToken -> existingToken.updateToken(token, expiresAt),
                        // 없으면 새로 생성
                        () -> {
                            RefreshToken refreshToken = RefreshToken.builder()
                                    .member(member)
                                    .token(token)
                                    .expiresAt(expiresAt)
                                    .build();
                            refreshTokenRepository.save(refreshToken);
                        });
    }
}
