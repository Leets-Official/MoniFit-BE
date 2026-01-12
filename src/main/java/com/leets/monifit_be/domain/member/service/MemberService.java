package com.leets.monifit_be.domain.member.service;

import com.leets.monifit_be.domain.auth.repository.RefreshTokenRepository;
import com.leets.monifit_be.domain.auth.service.KakaoOAuthService;
import com.leets.monifit_be.domain.member.dto.MemberNameUpdateRequest;
import com.leets.monifit_be.domain.member.dto.MemberResponse;
import com.leets.monifit_be.domain.member.entity.Member;
import com.leets.monifit_be.domain.member.repository.MemberRepository;
import com.leets.monifit_be.global.exception.BusinessException;
import com.leets.monifit_be.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 회원 서비스
 * 내 정보 조회, 이름 수정, 계정 삭제(탈퇴) 처리
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final KakaoOAuthService kakaoOAuthService;

    /**
     * 내 정보 조회
     *
     * @param memberId 회원 ID (JWT에서 추출)
     * @return 회원 정보 응답
     */
    @Transactional(readOnly = true)
    public MemberResponse getMyInfo(Long memberId) {
        Member member = findMemberById(memberId);
        log.info("내 정보 조회: memberId={}", memberId);
        return MemberResponse.from(member);
    }

    /**
     * 이름 수정
     *
     * @param memberId 회원 ID (JWT에서 추출)
     * @param request  이름 수정 요청
     * @return 수정된 회원 정보 응답
     */
    @Transactional
    public MemberResponse updateName(Long memberId, MemberNameUpdateRequest request) {
        Member member = findMemberById(memberId);
        member.updateName(request.getName());

        log.info("이름 수정 완료: memberId={}, newName={}", memberId, request.getName());
        return MemberResponse.from(member);
    }

    /**
     * 계정 삭제 (탈퇴)
     * 카카오 연동 해제 포함
     *
     * @param memberId 회원 ID (JWT에서 추출)
     */
    @Transactional
    public void deleteMember(Long memberId) {
        Member member = findMemberById(memberId);

        // 1. 카카오 연동 해제
        try {
            kakaoOAuthService.unlinkUser(member.getKakaoId());
            log.info("카카오 연동 해제 완료: kakaoId={}", member.getKakaoId());
        } catch (Exception e) {
            log.warn("카카오 연동 해제 실패 (계속 진행): kakaoId={}, error={}",
                    member.getKakaoId(), e.getMessage());
            // 카카오 연동 해제 실패해도 회원 삭제는 진행
        }

        // 2. 리프레시 토큰 삭제
        refreshTokenRepository.deleteByMemberId(memberId);

        // 3. 회원 삭제
        // - BudgetPeriod, Expense는 DB FK의 ON DELETE CASCADE로 함께 삭제됨
        // - DDL: FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE
        memberRepository.delete(member);

        log.info("회원 탈퇴 완료: memberId={}", memberId);
    }

    /**
     * 회원 ID로 회원 조회
     */
    private Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
    }
}
