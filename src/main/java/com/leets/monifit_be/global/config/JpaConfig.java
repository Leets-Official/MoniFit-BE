package com.leets.monifit_be.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.time.LocalDateTime;
import java.util.Optional;

@Configuration
@EnableJpaAuditing(dateTimeProviderRef = "auditingDateTimeProvider")
public class JpaConfig {

    /**
     * JPA Auditing에서 사용할 현재 시간 제공
     * 
     * TimezoneConfig에서 JVM 기본 시간대를 Asia/Seoul로 설정했으므로
     * LocalDateTime.now()는 자동으로 한국 시간을 반환합니다.
     * 
     * 참고: TimezoneConfig가 없어도 작동하도록 하려면
     * LocalDateTime.ofInstant(Instant.now(), ZoneId.of("Asia/Seoul"))을 사용하세요.
     */
    @Bean
    public DateTimeProvider auditingDateTimeProvider() {
        return () -> Optional.of(LocalDateTime.now());
    }
}
