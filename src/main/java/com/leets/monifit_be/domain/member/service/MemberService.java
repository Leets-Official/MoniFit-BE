package com.leets.monifit_be.domain.member.service;

import com.leets.monifit_be.domain.auth.repository.RefreshTokenRepository;
import com.leets.monifit_be.domain.auth.service.KakaoOAuthService;
import com.leets.monifit_be.domain.budget.entity.BudgetPeriod;
import com.leets.monifit_be.domain.budget.entity.PeriodStatus;
import com.leets.monifit_be.domain.budget.repository.BudgetPeriodRepository;
import com.leets.monifit_be.domain.expense.repository.ExpenseRepository;
import com.leets.monifit_be.domain.member.dto.MemberNameUpdateRequest;
import com.leets.monifit_be.domain.member.dto.MemberNameUpdateResponse;
import com.leets.monifit_be.domain.member.dto.MemberResponse;
import com.leets.monifit_be.domain.member.entity.Member;
import com.leets.monifit_be.domain.member.repository.MemberRepository;
import com.leets.monifit_be.global.exception.BusinessException;
import com.leets.monifit_be.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

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
    private final BudgetPeriodRepository budgetPeriodRepository;
    private final ExpenseRepository expenseRepository;

    /**
     * 내 정보 조회
     * 요구사항 9-1 마이페이지:
     * - 이메일, 이름, 가입일
     * - 시작일: 최초 목표 예산 설정 시작일
     * - 사용 기간, 절약/초과 통계
     *
     * @param memberId 회원 ID (JWT에서 추출)
     * @return 회원 정보 응답
     */
    @Transactional(readOnly = true)
    public MemberResponse getMyInfo(Long memberId) {
        Member member = findMemberById(memberId);

        // 예산 기간 설정 이력 확인
        boolean hasEverSetBudget = budgetPeriodRepository.existsByMemberId(memberId);

        // 최초 예산 기간 시작일 조회 (없으면 null)
        LocalDate firstBudgetStartDate = budgetPeriodRepository
                .findFirstByMemberIdOrderByStartDateAsc(memberId)
                .map(BudgetPeriod::getStartDate)
                .orElse(null);

        // 절약/초과 통계 계산 (완료된 기간만)
        List<BudgetPeriod> completedPeriods = budgetPeriodRepository
                .findByMemberIdAndStatusOrderByEndDateDesc(memberId, PeriodStatus.COMPLETED);

        int savedPeriodCount = 0;
        int exceededPeriodCount = 0;
        int totalSavedAmount = 0;

        for (BudgetPeriod period : completedPeriods) {
            // 해당 기간의 총 지출 합계 조회
            long totalSpent = expenseRepository.sumAmountByBudgetPeriod(period);
            int savedAmount = period.getBudgetAmount() - (int) totalSpent;

            if (savedAmount >= 0) {
                savedPeriodCount++;
                totalSavedAmount += savedAmount;
            } else {
                exceededPeriodCount++;
            }
        }

        log.info("내 정보 조회: memberId={}, hasEverSetBudget={}, savedPeriodCount={}, exceededPeriodCount={}",
                memberId, hasEverSetBudget, savedPeriodCount, exceededPeriodCount);

        return MemberResponse.from(
                member,
                hasEverSetBudget,
                firstBudgetStartDate,
                savedPeriodCount,
                exceededPeriodCount,
                totalSavedAmount);
    }

    /**
     * 이름 수정
     *
     * @param memberId 회원 ID (JWT에서 추출)
     * @param request  이름 수정 요청
     * @return 수정된 회원 정보 응답 (간단한 형식)
     */
    @Transactional
    public MemberNameUpdateResponse updateName(Long memberId, MemberNameUpdateRequest request) {
        Member member = findMemberById(memberId);
        member.updateName(request.getName());

        log.info("이름 수정 완료: memberId={}, newName={}", memberId, request.getName());
        return MemberNameUpdateResponse.from(member);
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
