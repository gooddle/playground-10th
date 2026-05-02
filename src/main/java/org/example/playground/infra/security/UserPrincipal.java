package org.example.playground.infra.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 인증된 사용자 정보 객체
 *
 * - 역할
 *   JwtAuthenticationToken 의 principal 로 저장되어 SecurityContextHolder 에서 꺼내 사용
 *   Controller / Service 에서 @AuthenticationPrincipal UserPrincipal principal 로 주입받음
 *
 * - 구조
 *   id        : DB 사용자 PK (Long)
 *   username  : 사용자 이름
 *   authorities : Spring Security 권한 목록 (ROLE_ 접두사 포함)
 *
 * - 커스텀 생성자
 *   Set<String> roles → "ROLE_" + role 형태의 SimpleGrantedAuthority 로 변환
 *   ex) "ADMIN" → "ROLE_ADMIN"
 */
public record UserPrincipal(
        Long id,
        String username,
        Collection<? extends GrantedAuthority> authorities
) {
    // 커스텀 생성자 (ID, 이름, 역할 세트를 받아서 변환)
    public UserPrincipal(Long id, String username, Set<String> roles) {
        this(
                id,
                username,
                roles.stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                        .collect(Collectors.toSet())
        );
    }
}
