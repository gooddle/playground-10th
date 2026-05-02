package org.example.playground.infra.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.example.playground.infra.security.UserPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.util.Optional;
import java.util.Set;
import static java.util.Arrays.stream;

/**
 * JWT 인증 필터
 *
 * - Filter (서블릿 컨테이너 레벨)
 *   클라이언트 → [JwtAuthenticationFilter] → DispatcherServlet → Controller
 *   - Spring Security FilterChain에 등록되어 모든 요청마다 1회 실행 (OncePerRequestFilter)
 *   - Controller 진입 전, 쿠키에서 accessToken을 꺼내 검증
 *
 * - 처리 흐름
 *   1. 쿠키에서 accessToken 추출
 *   2. JwtPlugin.validateToken() 으로 서명·만료 검증
 *   3. 검증 성공 시 UserPrincipal 생성 → JwtAuthenticationToken → SecurityContextHolder 저장
 *   4. 검증 실패(토큰 없음·만료·위조) 시 SecurityContext 저장 없이 다음 필터로 통과
 *      → 이후 인가 필터에서 401/403 처리
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtPlugin jwtPlugin;

    public JwtAuthenticationFilter(JwtPlugin jwtPlugin) {
        this.jwtPlugin = jwtPlugin;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) {
        String jwt = getJwtFromCookie(request);

        if (jwt != null) {
            Optional<Jws<Claims>> jwtClaims = jwtPlugin.validateToken(jwt);
            if (jwtClaims.isPresent()) {
                Jws<Claims> result = jwtClaims.get();
                Long userId = Long.parseLong(result.getBody().getSubject());
                String role = result.getBody().get("role", String.class);
                String username = result.getBody().get("username", String.class);

                UserPrincipal principal = new UserPrincipal(
                        userId,
                        username,
                        Set.of(role)
                );

                JwtAuthenticationToken authentication = new JwtAuthenticationToken(
                        principal,
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        try {
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String getJwtFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) return null;

        return stream(request.getCookies())
                .filter(cookie -> "accessToken".equals(cookie.getName()))
                .map(jakarta.servlet.http.Cookie::getValue)
                .findFirst()
                .orElse(null);
    }
}
