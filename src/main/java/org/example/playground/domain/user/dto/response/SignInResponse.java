package org.example.playground.domain.user.dto.response;

import org.example.playground.domain.user.model.User;

public record SignInResponse(
        Long userId,
        String email,
        String accessToken,
        String refreshToken
) {
    public static SignInResponse from(User user, String accessToken, String refreshToken) {
        return new SignInResponse(user.getId(), user.getEmail(), accessToken, refreshToken);
    }
}
