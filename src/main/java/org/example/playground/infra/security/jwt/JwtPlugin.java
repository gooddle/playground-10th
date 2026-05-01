package org.example.playground.infra.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

/**
 * JWT 토큰 생성 및 검증 유틸리티
 *
 * - 토큰 흐름
 *   로그인 성공 → generateAccessToken() → 쿠키에 담아 응답
 *   이후 요청  → validateToken() → Claims 파싱 → SecurityContext 저장
 *
 * - 설정값 (application.yml)
 *   auth.jwt.issuer                 : 발급자 식별자
 *   auth.jwt.secret                 : HMAC-SHA 서명 키 (256bit 이상 권장)
 *   auth.jwt.accessTokenExpirationHour : 액세스 토큰 만료 시간
 */
@Configuration
public class JwtPlugin {

    @Value("${auth.jwt.issuer}")
    private String issuer;

    @Value("${auth.jwt.secret}")
    private String secret;

    @Value("${auth.jwt.accessTokenExpirationHour}")
    private long accessTokenExpirationHour;

    @Value("${auth.jwt.refreshTokenExpirationDay}")
    private long refreshTokenExpirationDay;

    public Optional<Jws<Claims>> validateToken(String jwt) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(jwt);
            return Optional.of(claims);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public String generateAccessToken(String subject, String role, String userName) {
        return generateToken(subject, role, userName, Duration.ofHours(accessTokenExpirationHour));
    }

    public String generateRefreshToken(String subject) {
        return generateToken(subject, null, null, Duration.ofDays(refreshTokenExpirationDay));
    }

    private String generateToken(String subject, String role, String userName, Duration expirationPeriod) {
        Claims claims = Jwts.claims()
                .setSubject(subject)
                .setIssuer(issuer)
                .setIssuedAt(new Date())
                .setExpiration(Date.from(Instant.now().plus(expirationPeriod)));

        if (role != null) claims.put("role", role);
        if (userName != null) claims.put("userName", userName);

        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .setClaims(claims)
                .signWith(key)
                .compact();
    }
}
