package com.leets.monifit_be.domain.budget.repository;

import com.leets.monifit_be.domain.budget.entity.BudgetPeriod;
import com.leets.monifit_be.domain.budget.entity.PeriodStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * 예산 기간 Repository
 */
public interface BudgetPeriodRepository extends JpaRepository<BudgetPeriod, Long> {

    /**
     * 회원의 특정 상태 기간 조회
     * 활성 기간 조회에 사용
     */
    Optional<BudgetPeriod> findByMemberIdAndStatus(Long memberId, PeriodStatus status);

    /**
     * 회원의 특정 상태 기간 존재 여부 확인
     * 활성 기간 중복 생성 방지에 사용
     */
    boolean existsByMemberIdAndStatus(Long memberId, PeriodStatus status);

    /**
     * 회원의 특정 상태 기간 목록 조회 (종료일 기준 최신순)
     * 완료된 기간 목록 조회(리포트)에 사용
     */
    List<BudgetPeriod> findByMemberIdAndStatusOrderByEndDateDesc(Long memberId, PeriodStatus status);

    /**
     * 회원의 첫 번째(최초) 예산 기간 조회 (시작일 기준 오름차순, 1건)
     * 마이페이지 "시작일" 표시에 사용
     */
    Optional<BudgetPeriod> findFirstByMemberIdOrderByStartDateAsc(Long memberId);

    /**
     * 회원의 예산 기간 존재 여부 확인
     * 로그인 시 hasEverSetBudget 판단에 사용
     */
    boolean existsByMemberId(Long memberId);

    /**
     * 회원의 모든 예산 기간 조회 (시작일 기준 오름차순)
     * 스탬프 기간 탐색(이전/다음)에 사용
     */
    List<BudgetPeriod> findAllByMemberIdOrderByStartDateAsc(Long memberId);
}
