package org.example.playground.domain.user.dto.request;

import lombok.Getter;
import lombok.Setter;

/**
 * 회원가입 요청 본문
 *
 * email: 회원 가입 요청 이메일
 * password: 비밀번호
 */
@Getter
@Setter
public class SignUpRequest {
    private String email;
    private String password;
}
