package com.leets.monifit_be.domain.budget.service;

import com.leets.monifit_be.domain.budget.dto.BudgetPeriodCreateRequest;
import com.leets.monifit_be.domain.budget.dto.BudgetPeriodDetailResponse;
import com.leets.monifit_be.domain.budget.dto.BudgetPeriodResponse;
import com.leets.monifit_be.domain.budget.entity.BudgetPeriod;
import com.leets.monifit_be.domain.budget.entity.PeriodStatus;
import com.leets.monifit_be.domain.budget.repository.BudgetPeriodRepository;
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
import java.util.stream.Collectors;

/**
 * 예산 기간 서비스
 * - 예산 기간 생성, 조회, 목록 조회 처리
 *
 * 비즈니스 규칙:
 * 1. 한 회원당 활성(ACTIVE) 기간은 하나만 존재 가능
 * 2. 시작일은 오늘 이후(오늘 포함)만 가능
 * 3. 기간은 자동으로 시작일 + 29일 (총 30일)
 * 4. 활성 기간이 없으면 404 반환 (클라이언트가 목표 설정 화면으로 유도)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BudgetPeriodService {

    private final BudgetPeriodRepository budgetPeriodRepository;
    private final MemberRepository memberRepository;

    /**
     * 예산 기간 생성
     * POST /budget-periods
     *
     * @param memberId 회원 ID (JWT에서 추출)
     * @param request  생성 요청 (시작일, 예산 금액)
     * @return 생성된 예산 기간 정보
     */
    @Transactional
    public BudgetPeriodResponse create(Long memberId, BudgetPeriodCreateRequest request) {
        // 1. 회원 조회
        Member member = findMemberById(memberId);

        // 2. 이미 활성 기간이 있는지 확인
        if (budgetPeriodRepository.existsByMemberIdAndStatus(memberId, PeriodStatus.ACTIVE)) {
            log.warn("활성 기간 중복 생성 시도: memberId={}", memberId);
            throw new BusinessException(ErrorCode.ACTIVE_PERIOD_EXISTS);
        }

        // 3. 시작일 유효성 검증 (오늘 포함 이후만 가능)
        LocalDate today = LocalDate.now();
        if (request.getStartDate().isBefore(today)) {
            log.warn("잘못된 시작일: memberId={}, startDate={}, today={}",
                    memberId, request.getStartDate(), today);
            throw new BusinessException(ErrorCode.INVALID_START_DATE);
        }

        // 4. 예산 기간 생성 및 저장
        BudgetPeriod budgetPeriod = BudgetPeriod.builder()
                .member(member)
                .startDate(request.getStartDate())
                .budgetAmount(request.getBudgetAmount())
                .build();

        BudgetPeriod savedPeriod = budgetPeriodRepository.save(budgetPeriod);

        log.info("예산 기간 생성 완료: memberId={}, periodId={}, startDate={}, endDate={}, budget={}",
                memberId, savedPeriod.getId(), savedPeriod.getStartDate(),
                savedPeriod.getEndDate(), savedPeriod.getBudgetAmount());

        return BudgetPeriodResponse.from(savedPeriod);
    }

    /**
     * 활성 예산 기간 조회
     * GET /budget-periods/active
     *
     * 활성 기간이 없으면 404 반환
     * → 클라이언트는 404 응답 시 목표 설정 화면으로 이동
     *
     * @param memberId 회원 ID (JWT에서 추출)
     * @return 활성 예산 기간 정보
     */
    @Transactional(readOnly = true)
    public BudgetPeriodResponse getActivePeriod(Long memberId) {
        BudgetPeriod activePeriod = budgetPeriodRepository
                .findByMemberIdAndStatus(memberId, PeriodStatus.ACTIVE)
                .orElseThrow(() -> {
                    log.info("활성 기간 없음: memberId={}", memberId);
                    return new BusinessException(ErrorCode.ACTIVE_PERIOD_NOT_FOUND);
                });

        log.info("활성 기간 조회: memberId={}, periodId={}", memberId, activePeriod.getId());
        return BudgetPeriodResponse.from(activePeriod);
    }

    /**
     * 완료된 기간 목록 조회 (리포트용)
     * GET /budget-periods/completed
     *
     * 종료일 기준 최신순으로 정렬
     *
     * @param memberId 회원 ID (JWT에서 추출)
     * @return 완료된 예산 기간 목록
     */
    @Transactional(readOnly = true)
    public List<BudgetPeriodResponse> getCompletedPeriods(Long memberId) {
        List<BudgetPeriod> completedPeriods = budgetPeriodRepository
                .findByMemberIdAndStatusOrderByEndDateDesc(memberId, PeriodStatus.COMPLETED);

        log.info("완료된 기간 목록 조회: memberId={}, count={}", memberId, completedPeriods.size());

        return completedPeriods.stream()
                .map(BudgetPeriodResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 특정 기간 상세 조회 (리포트 상세)
     * GET /budget-periods/{periodId}
     *
     * 본인의 기간만 조회 가능
     *
     * @param memberId 회원 ID (JWT에서 추출)
     * @param periodId 조회할 기간 ID
     * @return 예산 기간 상세 정보 (지출 집계 포함)
     */
    @Transactional(readOnly = true)
    public BudgetPeriodDetailResponse getPeriodDetail(Long memberId, Long periodId) {
        // 1. 예산 기간 조회
        BudgetPeriod budgetPeriod = budgetPeriodRepository.findById(periodId)
                .orElseThrow(() -> {
                    log.warn("예산 기간 없음: periodId={}", periodId);
                    return new BusinessException(ErrorCode.BUDGET_PERIOD_NOT_FOUND);
                });

        // 2. 본인 기간인지 검증 (다른 사람 기간은 "없음"으로 처리 - 보안)
        if (!budgetPeriod.getMember().getId().equals(memberId)) {
            log.warn("권한 없는 기간 조회 시도: memberId={}, periodId={}, ownerId={}",
                    memberId, periodId, budgetPeriod.getMember().getId());
            throw new BusinessException(ErrorCode.BUDGET_PERIOD_NOT_FOUND);
        }

        // 3. 총 지출 계산
        // TODO: Expense 구현 후 실제 지출 합계로 대체
        Integer totalExpense = 0;

        log.info("기간 상세 조회: memberId={}, periodId={}", memberId, periodId);
        return BudgetPeriodDetailResponse.from(budgetPeriod, totalExpense);
    }

    /**
     * 회원 ID로 회원 조회
     */
    private Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
    }
}
