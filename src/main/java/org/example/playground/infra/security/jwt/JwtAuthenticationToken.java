package org.example.playground.infra.security.jwt;

import org.example.playground.infra.security.UserPrincipal;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import java.io.Serializable;

/**
 * JWT 인증 객체
 *
 * - 역할
 *   JwtAuthenticationFilter 에서 토큰 검증 성공 후 생성되어 SecurityContextHolder 에 저장됨
 *   Spring Security가 인증 여부를 판단할 때 이 객체를 참조
 *
 * - 구조
 *   AbstractAuthenticationToken 상속 → Spring Security 인증 체계에 통합
 *   principal : 인증된 사용자 정보 (UserPrincipal - userId, username, roles)
 *   credentials : JWT는 토큰 자체가 자격증명이므로 null 반환
 *   authenticated : 생성 시점에 true 고정 (이미 검증된 토큰에서 만든 객체이므로)
 */
public class JwtAuthenticationToken extends AbstractAuthenticationToken implements Serializable {

    private final UserPrincipal principal;

    public JwtAuthenticationToken(UserPrincipal principal, WebAuthenticationDetails detail) {
        super(principal.authorities());
        this.principal = principal;
        super.setAuthenticated(true);
        super.setDetails(detail);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public UserPrincipal getPrincipal() {
        return principal;
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }
}
