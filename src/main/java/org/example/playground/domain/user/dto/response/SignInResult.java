package org.example.playground.domain.user.dto.response;

import org.example.playground.domain.user.model.User;

public record SignInResult(String accessToken, String refreshToken, User user) {
}
