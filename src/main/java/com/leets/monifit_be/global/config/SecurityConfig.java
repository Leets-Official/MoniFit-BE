package com.leets.monifit_be.global.config;

import com.leets.monifit_be.global.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

        private final JwtAuthenticationFilter jwtAuthenticationFilter;

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http
                                // CSRF 비활성화 (JWT 사용)
                                .csrf(AbstractHttpConfigurer::disable)

                                // 세션 사용 안 함 (Stateless)
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                                // 요청 권한 설정
                                .authorizeHttpRequests(auth -> auth
                                                // 인증 없이 접근 가능한 경로
                                                .requestMatchers(
                                                                "/api/v1/auth/kakao/login",
                                                                "/api/v1/auth/reissue",
                                                                "/health",
                                                                "/error",
                                                                // Swagger
                                                                "/swagger-ui/**",
                                                                "/swagger-ui.html",
                                                                "/v3/api-docs/**")
                                                .permitAll()
                                                // 나머지는 인증 필요
                                                .anyRequest().authenticated())

                                // JWT 필터 추가
                                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }
}
