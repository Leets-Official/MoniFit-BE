package com.leets.monifit_be.domain.stamp.controller;

import com.leets.monifit_be.domain.stamp.dto.StampResponse;
import com.leets.monifit_be.domain.stamp.service.StampService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/stamps")
public class StampController {

    private final StampService stampService;

    // 생성자 주입
    public StampController(StampService stampService) {
        this.stampService = stampService;
    }

    @GetMapping
    public ResponseEntity<StampResponse> getMonthlyStamps(
            @RequestParam int year,
            @RequestParam int month) {

        // 실제 구현 시 인증 객체에서 memberId를 추출해야 합니다.
        Long memberId = 1L;

        StampResponse response = stampService.getMonthlyStamps(memberId, year, month);
        return ResponseEntity.ok(response);
    }
}