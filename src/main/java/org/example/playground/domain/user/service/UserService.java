package org.example.playground.domain.user.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.playground.domain.user.constant.UserRole;
import org.example.playground.domain.user.dto.request.SignInRequest;
import org.example.playground.domain.user.dto.request.SignUpRequest;
import org.example.playground.domain.user.dto.response.RefreshResult;
import org.example.playground.domain.user.dto.response.SignInResult;
import org.example.playground.domain.user.dto.response.SignUpResponse;
import org.example.playground.domain.user.model.User;
import org.example.playground.domain.user.repository.UserRepository;
import org.example.playground.infra.redis.RefreshTokenService;
import org.example.playground.infra.security.jwt.JwtPlugin;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtPlugin jwtPlugin;
    private final RefreshTokenService refreshTokenService;

    @Value("${auth.jwt.refreshTokenExpirationDay}")
    private long refreshTokenExpirationDay;

    /**
     * 회원가입 요청
     * @param request 회원가입 요청 본문
     * @throws IllegalArgumentException 회원가입 이메일이 중복일 경우
     * @return 생성된 회원 정보
     */
    @Transactional
    public SignUpResponse signUp(SignUpRequest request) {
        userRepository.findByEmail(request.getEmail())
                .ifPresent(_ -> {
                    throw new IllegalArgumentException("이미 가입된 이메일입니다: " + request.getEmail());
                });
        User newUser = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.NORMAL.name())
                .createdAt(LocalDateTime.now())
                .build();
        User savedUser = userRepository.save(newUser);
        return new SignUpResponse(savedUser.getId(), "회원가입이 완료되었습니다.");
    }

    /**
     * 로그인 요청
     * @param request 로그인 요청 본문
     * @throws IllegalArgumentException 이메일 또는 비밀번호가 일치하지 않을 경우
     * @return 생성된 액세스 토큰 + 유저 정보 (Controller에서 토큰은 쿠키, 유저 정보는 응답 바디로 분리)
     */
    @Transactional
    public SignInResult signIn(SignInRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("이메일 또는 비밀번호가 일치하지 않습니다."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 일치하지 않습니다.");
        }

        String accessToken = jwtPlugin.generateAccessToken(
                user.getId().toString(),
                user.getRole(),
                user.getEmail()
        );

        String refreshToken = jwtPlugin.generateRefreshToken(user.getId().toString());
        refreshTokenService.save(user.getId().toString(), refreshToken, refreshTokenExpirationDay);

        return new SignInResult(accessToken, refreshToken, user);
    }

    public void signOut(String userId) {
        refreshTokenService.delete(userId);
    }

    /**
     * accessToken + refreshToken 재발급 (Refresh Token Rotation)
     * - refreshToken 검증 → 유효하면 새 accessToken + 새 refreshToken 발급
     * - 기존 refreshToken은 Redis에서 삭제 → 탈취된 토큰 재사용 차단
     * @throws BadCredentialsException refreshToken 유효하지 않을 경우
     */
    public RefreshResult refresh(String refreshToken) {
        String userId = jwtPlugin.validateToken(refreshToken)
                .map(claims -> claims.getBody().getSubject())
                .orElseThrow(() -> new BadCredentialsException("유효하지 않은 토큰입니다."));

        if (!refreshTokenService.isValid(userId, refreshToken)) {
            throw new BadCredentialsException("유효하지 않은 토큰입니다.");
        }

        User user = userRepository.findById(Long.parseLong(userId))
                .orElseThrow(() -> new BadCredentialsException("유효하지 않은 토큰입니다."));

        String newAccessToken = jwtPlugin.generateAccessToken(userId, user.getRole(), user.getEmail());
        String newRefreshToken = jwtPlugin.generateRefreshToken(userId);

        // 기존 refreshToken 삭제 후 새 refreshToken 저장
        refreshTokenService.save(userId, newRefreshToken, refreshTokenExpirationDay);

        return new RefreshResult(newAccessToken, newRefreshToken);
    }
}
