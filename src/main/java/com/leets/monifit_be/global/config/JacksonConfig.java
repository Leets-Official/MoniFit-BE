package com.leets.monifit_be.global.config;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

/**
 * Jackson ObjectMapper 전역 설정
 * - Enum 값을 대소문자 구분 없이 역직렬화 가능하도록 설정
 */
@Configuration
public class JacksonConfig {

    @Bean
    @Primary
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        // Spring Boot의 기본 설정(JavaTimeModule 등)을 유지하면서 ObjectMapper 생성
        ObjectMapper mapper = builder.build();

        // Enum 값을 대소문자 구분 없이 받아들임
        // 예: "food" -> FOOD, "Food" -> FOOD 모두 허용
        mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS, true);

        return mapper;
    }
}
