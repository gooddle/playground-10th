package org.example.playground.infra.security;

import org.example.playground.infra.security.jwt.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 설정
 *
 * - 역할
 *   애플리케이션 전체의 인증·인가 정책을 한 곳에서 정의
 *
 * - 필터 체인 구성
 *   CSRF / HTTP Basic / Form Login 비활성화 (JWT 쿠키 방식 사용)
 *   permitAll : 로그인·회원가입·Swagger 등 인증 없이 접근 가능한 경로
 *   authenticated : 그 외 모든 요청은 인증 필요
 *
 * - 필터 순서
 *   JwtAuthenticationFilter → UsernamePasswordAuthenticationFilter
 *   → JWT 검증을 먼저 수행해 SecurityContext에 인증 정보를 저장
 *
 * - 예외 처리
 *   인증 실패(401) 시 CustomAuthenticationEntryPoint 에서 JSON 응답 반환
 *
 * - @EnableMethodSecurity
 *   @PreAuthorize, @PostAuthorize 등 메서드 레벨 인가 활성화
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, CustomAuthenticationEntryPoint authenticationEntryPoint) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.customAuthenticationEntryPoint = authenticationEntryPoint;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        http
                .csrf(AbstractHttpConfigurer::disable)                         // CSRF 비활성화
                .httpBasic(AbstractHttpConfigurer::disable)           // HTTP Basic 비활성화
                .formLogin(AbstractHttpConfigurer::disable)           // Form Login 비활성화
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/",
                                "/login",
                                "/signup",
                                "/favicon.ico",
                                "/index.html",
                                "/api/v1/users/signIn",
                                "/api/v1/users/signup",
                                "/api/v1/users/refresh",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/api/v1/send-email-code"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(it -> it.authenticationEntryPoint(customAuthenticationEntryPoint));


        return http.build();
    }
}