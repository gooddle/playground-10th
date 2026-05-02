package org.example.playground.infra.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.playground.common.dto.ErrorResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 인증 실패 진입점 (401 Unauthorized 처리)
 *
 * - 역할
 *   인증되지 않은 요청이 보호된 리소스에 접근할 때 호출됨
 *   Spring Security 기본 동작(리다이렉트·HTML 응답) 대신 JSON 형식으로 에러 반환
 *
 * - 호출 시점
 *   JwtAuthenticationFilter 에서 토큰 검증 실패 → SecurityContext 미저장
 *   → 이후 인가 필터에서 AuthenticationException 발생 → commence() 호출
 *
 * - 응답
 *   HTTP 401 / Content-Type: application/json
 */
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(@NonNull HttpServletRequest request, HttpServletResponse response, @NonNull AuthenticationException authException) throws IOException {
        // HTTP 상태 코드 설정 (인증 실패 시 401 Unauthorized)
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        // 오류 메시지 작성
        String message = authException.getMessage();

        ErrorResponse errorResponse = ErrorResponse.from(message);

        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
