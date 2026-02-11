package com.leets.monifit_be.global.config;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.util.TimeZone;

/**
 * JVM 기본 시간대를 Asia/Seoul로 설정
 * 
 * 이 설정은 애플리케이션 전체의 기본 시간대를 한국으로 설정합니다.
 * - LocalDateTime.now() 호출 시 한국 시간 반환
 * - JDBC serverTimezone과 일치하여 시간대 변환 문제 방지
 * - EC2 시스템 시간대(UTC)와 무관하게 작동
 */
@Configuration
public class TimezoneConfig {

    @PostConstruct
    public void setDefaultTimezone() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
    }
}
