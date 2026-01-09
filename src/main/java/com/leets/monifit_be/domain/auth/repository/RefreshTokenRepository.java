package com.leets.monifit_be.domain.auth.repository;

import com.leets.monifit_be.domain.auth.entity.RefreshToken;
import com.leets.monifit_be.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    /**
     * 회원으로 리프레시 토큰 조회
     */
    Optional<RefreshToken> findByMember(Member member);

    /**
     * 회원 ID로 리프레시 토큰 조회
     */
    Optional<RefreshToken> findByMemberId(Long memberId);

    /**
     * 토큰 문자열로 리프레시 토큰 조회
     * 토큰 재발급 시 사용
     */
    Optional<RefreshToken> findByToken(String token);

    /**
     * 회원의 리프레시 토큰 삭제 (로그아웃 시 사용)
     */
    @Modifying
    void deleteByMember(Member member);

    /**
     * 회원 ID로 리프레시 토큰 삭제
     */
    @Modifying
    void deleteByMemberId(Long memberId);
}
