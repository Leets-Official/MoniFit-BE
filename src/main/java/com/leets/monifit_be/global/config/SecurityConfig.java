package com.leets.monifit_be.global.config;

import com.leets.monifit_be.global.jwt.JwtAuthenticationEntryPoint;
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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

        private final JwtAuthenticationFilter jwtAuthenticationFilter;
        private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http
                                // CORS 설정 적용
                                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                                // CSRF 비활성화 (JWT 사용)
                                .csrf(AbstractHttpConfigurer::disable)

                                // 세션 사용 안 함 (Stateless)
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                                // 인증 실패 시 JSON 응답 반환
                                .exceptionHandling(exception -> exception
                                                .authenticationEntryPoint(jwtAuthenticationEntryPoint))

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
                                                                "/v3/api-docs/**",
                                                                "/api/v1/expenses/**",
                                                                "/api/v1/calendar/**",
                                                                "/api/v1/dashboard/**",
                                                                "/api/v1/stamps/**"
                                                                )
                                                .permitAll()
                                                // 나머지는 인증 필요
                                                .anyRequest().authenticated())

                                // JWT 필터 추가
                                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }

        /**
         * CORS 설정
         */
        private CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration configuration = new CorsConfiguration();

                // 허용할 Origin (프론트엔드 주소)
                configuration.setAllowedOrigins(Arrays.asList(
                                "http://localhost:3000",
                                "http://localhost:5173",
                                "https://monifit.com"));

                // 허용할 HTTP 메서드
                configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

                // 허용할 헤더
                configuration.setAllowedHeaders(List.of("*"));

                // 인증 정보 포함 허용
                configuration.setAllowCredentials(true);

                // 노출할 헤더
                configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));

                // preflight 캐시 시간
                configuration.setMaxAge(3600L);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);
                return source;
        }
}
