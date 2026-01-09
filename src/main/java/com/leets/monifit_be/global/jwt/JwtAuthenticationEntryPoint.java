package com.leets.monifit_be.global.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leets.monifit_be.global.response.ApiResponse;
import com.leets.monifit_be.global.response.ApiResponse.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * JWT 인증 실패 시 JSON 응답 반환
 * 401 Unauthorized 에러를 일관된 API 형식으로 반환
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {

        log.warn("인증 실패: {}", authException.getMessage());

        ErrorResponse error = new ErrorResponse("UNAUTHORIZED", "인증이 필요합니다");
        ApiResponse<Void> apiResponse = ApiResponse.error(error);

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }
}
