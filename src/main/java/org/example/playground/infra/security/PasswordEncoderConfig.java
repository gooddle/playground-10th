package org.example.playground.infra.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 비밀번호 암호화 설정
 *
 * - BCryptPasswordEncoder
 *   단방향 해시 알고리즘(BCrypt) 사용 → 원문 복원 불가
 *   내부적으로 랜덤 salt를 포함해 동일한 비밀번호도 매번 다른 해시값 생성
 *   기본 강도(cost factor) 10 → 해시 연산에 약간의 지연을 줘서 브루트포스 방어
 *
 * - 사용 위치
 *   회원가입 시 비밀번호 저장: encoder.encode(rawPassword)
 *   로그인 시 비밀번호 검증: encoder.matches(rawPassword, encodedPassword)
 */
@Configuration
public class PasswordEncoderConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        //기본 강도 10
        return new BCryptPasswordEncoder();
    }
}
