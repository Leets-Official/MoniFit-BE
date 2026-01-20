package com.leets.monifit_be.domain.calendar.controller;

import com.leets.monifit_be.domain.calendar.dto.CalendarResponse;
import com.leets.monifit_be.domain.calendar.service.CalendarService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/calendar")
public class CalendarController {

    private final CalendarService calendarService;

    public CalendarController(CalendarService calendarService) {
        this.calendarService = calendarService;
    }

    @GetMapping
    public ResponseEntity<CalendarResponse> getDailyDetail(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        Long tempMemberId = 1L;
        return ResponseEntity.ok(calendarService.getDailyExpenses(tempMemberId,date));
    }
}