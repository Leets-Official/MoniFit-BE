package com.leets.monifit_be.domain.member.repository;

import com.leets.monifit_be.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    /**
     * 카카오 ID로 회원 조회
     * 카카오 로그인 시 기존 회원인지 확인하는 용도
     */
    Optional<Member> findByKakaoId(Long kakaoId);

    /**
     * 카카오 ID로 회원 존재 여부 확인
     */
    boolean existsByKakaoId(Long kakaoId);
}
