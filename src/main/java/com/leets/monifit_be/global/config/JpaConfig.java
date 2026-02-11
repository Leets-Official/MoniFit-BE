package com.leets.monifit_be.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

@Configuration
@EnableJpaAuditing(dateTimeProviderRef = "auditingDateTimeProvider")
public class JpaConfig {

    private static final ZoneId KOREA_ZONE = ZoneId.of("Asia/Seoul");

    /**
     * JPA Auditing에서 사용할 시간을 한국 시간대(Asia/Seoul) 기준으로 생성
     * 
     * Instant.now()를 사용하여 현재 순간(UTC)을 가져온 후
     * Asia/Seoul 시간대로 변환하여 LocalDateTime 반환
     * 
     * 이 방식은 서버 시스템 시간대나 JDBC serverTimezone 설정과 무관하게
     * 항상 정확한 한국 시간을 생성합니다.
     */
    @Bean
    public DateTimeProvider auditingDateTimeProvider() {
        return () -> Optional.of(
                LocalDateTime.ofInstant(Instant.now(), KOREA_ZONE));
    }
}
