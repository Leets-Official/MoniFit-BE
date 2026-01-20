package com.leets.monifit_be.domain.stamp.service;

import com.leets.monifit_be.domain.expense.repository.ExpenseRepository;
import com.leets.monifit_be.domain.stamp.dto.StampResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class StampService {

    private final ExpenseRepository expenseRepository;

    // 생성자 주입
    public StampService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    @Transactional(readOnly = true)
    public StampResponse getMonthlyStamps(Long memberId, int year, int month) {
        // Repository의 커스텀 쿼리를 호출하여 해당 월의 지출 날짜 리스트를 가져옴
        List<LocalDate> dates = expenseRepository.findDatesByMemberAndMonth(memberId, year, month);

        // DTO에 담아서 반환
        return new StampResponse(dates);
    }
}