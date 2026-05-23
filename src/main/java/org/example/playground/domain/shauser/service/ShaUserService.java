package org.example.playground.domain.shauser.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.playground.domain.shauser.constant.ShaUserRole;
import org.example.playground.domain.shauser.dto.request.ShaSignInRequest;
import org.example.playground.domain.shauser.dto.request.ShaSignUpRequest;
import org.example.playground.domain.shauser.dto.response.ShaRefreshResult;
import org.example.playground.domain.shauser.dto.response.ShaSignInResult;
import org.example.playground.domain.shauser.dto.response.ShaSignUpResponse;
import org.example.playground.domain.shauser.model.ShaUser;
import org.example.playground.domain.shauser.repository.ShaUserRepository;
import org.example.playground.infra.redis.RefreshTokenService;
import org.example.playground.infra.security.jwt.JwtPlugin;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HexFormat;

@Service
@RequiredArgsConstructor
public class ShaUserService {

    private final ShaUserRepository shaUserRepository;
    private final JwtPlugin jwtPlugin;
    private final RefreshTokenService refreshTokenService;

    @Value("${auth.jwt.refreshTokenExpirationDay}")
    private long refreshTokenExpirationDay;

    // ================================================================
    // 내부 유틸
    // ================================================================

    private String hashWithSha256(String raw) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encoded = digest.digest(raw.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(encoded);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 알고리즘을 찾을 수 없습니다.", e);
        }
    }

    // ================================================================
    // 회원가입
    // ================================================================

    @Transactional
    public ShaSignUpResponse signUp(ShaSignUpRequest request) {
        shaUserRepository.findByEmail(request.getEmail())
                .ifPresent(_ -> {
                    throw new IllegalArgumentException("이미 가입된 이메일입니다: " + request.getEmail());
                });
        ShaUser newUser = ShaUser.builder()
                .email(request.getEmail())
                .password(hashWithSha256(request.getPassword()))
                .role(ShaUserRole.NORMAL.name())
                .createdAt(LocalDateTime.now())
                .build();
        ShaUser savedUser = shaUserRepository.save(newUser);
        return new ShaSignUpResponse(savedUser.getId(), "회원가입이 완료되었습니다.");
    }

    // ================================================================
    // 로그인 - DB 식별
    // email + SHA-256(password) 를 WHERE 조건으로 DB에서 한 번에 조회
    // SELECT ... WHERE email = ? AND password = ?
    // ================================================================

    @Transactional
    public ShaSignInResult signInByDb(ShaSignInRequest request) {
        ShaUser user = shaUserRepository.findByEmailAndPassword(
                request.getEmail(), hashWithSha256(request.getPassword()))
                .orElseThrow(() -> new IllegalArgumentException("이메일 또는 비밀번호가 일치하지 않습니다."));

        return generateSignInResult(user);
    }

    // ================================================================
    // 로그인 - DB 해싱 + DB 식별
    // 평문 비밀번호를 넘기면 DB가 SHA2(:password, 256) 으로 해싱 + 식별 동시 처리
    // SELECT ... WHERE email = ? AND password = SHA2(?, 256)
    // ================================================================

    @Transactional
    public ShaSignInResult signInByDbHash(ShaSignInRequest request) {
        ShaUser user = shaUserRepository.findByEmailWithDbHash(
                request.getEmail(), request.getPassword())
                .orElseThrow(() -> new IllegalArgumentException("이메일 또는 비밀번호가 일치하지 않습니다."));

        return generateSignInResult(user);
    }

    // ================================================================
    // 로그인 - Java 식별
    // email 로만 DB 조회 후 Java 에서 비밀번호 비교
    // SELECT ... WHERE email = ?  →  hashWithSha256(입력값).equals(DB값)
    // ================================================================

    @Transactional
    public ShaSignInResult signInByJava(ShaSignInRequest request) {
        ShaUser user = shaUserRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("이메일 또는 비밀번호가 일치하지 않습니다."));

        if (!hashWithSha256(request.getPassword()).equals(user.getPassword())) {
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

    public ShaRefreshResult refresh(String refreshToken) {
        String userId = jwtPlugin.validateToken(refreshToken)
                .map(claims -> claims.getBody().getSubject())
                .orElseThrow(() -> new BadCredentialsException("유효하지 않은 토큰입니다."));

        if (!refreshTokenService.isValid(userId, refreshToken)) {
            throw new BadCredentialsException("유효하지 않은 토큰입니다.");
        }

        ShaUser user = shaUserRepository.findById(Long.parseLong(userId))
                .orElseThrow(() -> new BadCredentialsException("유효하지 않은 토큰입니다."));

        String newAccessToken = jwtPlugin.generateAccessToken(userId, user.getRole(), user.getEmail(), "SHA");
        String newRefreshToken = jwtPlugin.generateRefreshToken(userId);
        refreshTokenService.save(userId, newRefreshToken, refreshTokenExpirationDay);

        return new ShaRefreshResult(newAccessToken, newRefreshToken);
    }

    // ================================================================
    // 공통 - 토큰 생성 및 결과 반환
    // ================================================================

    private ShaSignInResult generateSignInResult(ShaUser user) {
        String accessToken = jwtPlugin.generateAccessToken(
                user.getId().toString(),
                user.getRole(),
                user.getEmail(),
                "SHA"
        );
        String refreshToken = jwtPlugin.generateRefreshToken(user.getId().toString());
        refreshTokenService.save(user.getId().toString(), refreshToken, refreshTokenExpirationDay);

        return new ShaSignInResult(accessToken, refreshToken, user);
    }
}
