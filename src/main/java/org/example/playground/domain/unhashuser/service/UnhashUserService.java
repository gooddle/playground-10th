package org.example.playground.domain.unhashuser.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.playground.domain.unhashuser.constant.UnhashUserRole;
import org.example.playground.domain.unhashuser.dto.request.UnhashSignInRequest;
import org.example.playground.domain.unhashuser.dto.request.UnhashSignUpRequest;
import org.example.playground.domain.unhashuser.dto.response.UnhashRefreshResult;
import org.example.playground.domain.unhashuser.dto.response.UnhashSignInResult;
import org.example.playground.domain.unhashuser.dto.response.UnhashSignUpResponse;
import org.example.playground.domain.unhashuser.model.UnhashUser;
import org.example.playground.domain.unhashuser.repository.UnhashUserRepository;
import org.example.playground.infra.redis.RefreshTokenService;
import org.example.playground.infra.security.jwt.JwtPlugin;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UnhashUserService {

    private final UnhashUserRepository unhashUserRepository;
    private final JwtPlugin jwtPlugin;
    private final RefreshTokenService refreshTokenService;

    @Value("${auth.jwt.refreshTokenExpirationDay}")
    private long refreshTokenExpirationDay;

    // ================================================================
    // 회원가입
    // ================================================================

    @Transactional
    public UnhashSignUpResponse signUp(UnhashSignUpRequest request) {
        unhashUserRepository.findByEmail(request.getEmail())
                .ifPresent(_ -> {
                    throw new IllegalArgumentException("이미 가입된 이메일입니다: " + request.getEmail());
                });
        UnhashUser newUser = UnhashUser.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .role(UnhashUserRole.NORMAL.name())
                .createdAt(LocalDateTime.now())
                .build();
        UnhashUser savedUser = unhashUserRepository.save(newUser);
        return new UnhashSignUpResponse(savedUser.getId(), "회원가입이 완료되었습니다.");
    }

    // ================================================================
    // 로그인 - DB 식별
    // email + password 를 WHERE 조건으로 DB에서 한 번에 조회 (평문 비교)
    // SELECT ... WHERE email = ? AND password = ?
    // ================================================================

    @Transactional
    public UnhashSignInResult signInByDb(UnhashSignInRequest request) {
        UnhashUser user = unhashUserRepository.findByEmailAndPassword(
                request.getEmail(), request.getPassword())
                .orElseThrow(() -> new IllegalArgumentException("이메일 또는 비밀번호가 일치하지 않습니다."));

        return generateSignInResult(user);
    }

    // ================================================================
    // 로그인 - Java 식별
    // email 로만 DB 조회 후 Java 에서 비밀번호 비교 (평문 비교)
    // SELECT ... WHERE email = ?  →  입력값.equals(DB값)
    // ================================================================

    @Transactional
    public UnhashSignInResult signInByJava(UnhashSignInRequest request) {
        UnhashUser user = unhashUserRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("이메일 또는 비밀번호가 일치하지 않습니다."));

        if (!request.getPassword().equals(user.getPassword())) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 일치하지 않습니다.");
        }

        return generateSignInResult(user);
    }

    // ================================================================
    // 로그아웃
    // ================================================================

    public void signOut(String userId) {
        refreshTokenService.delete(userId);
    }

    // ================================================================
    // 토큰 재발급
    // ================================================================

    public UnhashRefreshResult refresh(String refreshToken) {
        String userId = jwtPlugin.validateToken(refreshToken)
                .map(claims -> claims.getBody().getSubject())
                .orElseThrow(() -> new BadCredentialsException("유효하지 않은 토큰입니다."));

        if (!refreshTokenService.isValid(userId, refreshToken)) {
            throw new BadCredentialsException("유효하지 않은 토큰입니다.");
        }

        UnhashUser user = unhashUserRepository.findById(Long.parseLong(userId))
                .orElseThrow(() -> new BadCredentialsException("유효하지 않은 토큰입니다."));

        String newAccessToken = jwtPlugin.generateAccessToken(userId, user.getRole(), user.getEmail(), "UNHASH");
        String newRefreshToken = jwtPlugin.generateRefreshToken(userId);
        refreshTokenService.save(userId, newRefreshToken, refreshTokenExpirationDay);

        return new UnhashRefreshResult(newAccessToken, newRefreshToken);
    }

    // ================================================================
    // 공통 - 토큰 생성 및 결과 반환
    // ================================================================

    private UnhashSignInResult generateSignInResult(UnhashUser user) {
        String accessToken = jwtPlugin.generateAccessToken(
                user.getId().toString(),
                user.getRole(),
                user.getEmail(),
                "UNHASH"
        );
        String refreshToken = jwtPlugin.generateRefreshToken(user.getId().toString());
        refreshTokenService.save(user.getId().toString(), refreshToken, refreshTokenExpirationDay);

        return new UnhashSignInResult(accessToken, refreshToken, user);
    }
}
